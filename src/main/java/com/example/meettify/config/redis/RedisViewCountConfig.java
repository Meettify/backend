package com.example.meettify.config.redis;

import com.example.meettify.config.redis.cookie.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
 *   worker : 유요한
 *   work   : RedisService 클래스는 Redis와 통신하여 데이터를 저장하고 조작하는 여러 기능을 제공하는 서비스 계층
 *             여기서 @Service을 사용하는 이유는 순히 데이터를 저장하거나 조회하는 것이 아니라 다음과 같은 비즈니스 로직의 일부로 조회수 증가 로직을 사용하기 때문에
 *   date   : 2025/05/04
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisViewCountConfig {
    private final RedisTemplate<String, Object> redisTemplate;

    // 레디스에 저장된 특정 키의 값을 증가시키는 기능 수행
    // key에 해당하는 값(정수형)을 1만큼 증가시키는 것
    public void increaseViewCount(String key, Long id) {
        Long increaseValue = redisTemplate
                .opsForValue()
                .increment(key);


        // ✅ ZSet 조회수 랭킹도 함께 증가시킴
        // zsetKey: 예를 들면 "community:view:zset"
        // communityId: 증가할 게시글 ID
        // 점수(score): Redis 내부적으로 정렬 기준이 됨 → 즉, 조회수
        Double communityNewScore = redisTemplate.opsForZSet()
                .incrementScore("community:view:zset", String.valueOf(id), 1);

        log.debug("ZSet 업데이트 - community:view:zset, id={}, newScore={}", id, communityNewScore);

        Long saveId = null;

        // 스케줄러가 업데이트할 ID만 저장해두기 위해 Set으로 관리
        // "community:view:set" 에는 조회수가 실제로 증가된 커뮤니티 ID만 들어갑니다.
        if(key.contains("community")) {
            saveId = redisTemplate
                    .opsForSet()
                    .add("community:view:set", String.valueOf(id));
        }


        // ✅ ZSet 조회수 랭킹도 함께 증가시킴
        Double meetBoardNewScore =  redisTemplate.opsForZSet()
                .incrementScore("meetBoard:view:zset", String.valueOf(id), 1);

        if(key.contains("meetBoard")) {
            saveId = redisTemplate
                    .opsForSet()
                    .add("meetBoard:view:set", String.valueOf(id));
        }

        log.debug("ZSet 업데이트 - meetBoard:view:zset, id={}, newScore={}", id, meetBoardNewScore);
        log.debug("Increased value for key {}: {}", key, increaseValue);
        // saveId == 1: 새로 등록됨 → 스케줄러가 이 ID를 처리할 것
        // saveId == 0: 이미 등록된 ID → 중복 처리 방지됨
        log.debug("[스케줄러용 ID Set 등록] key={}, id={}, wasAdded={}", "community:view:set", id, saveId);

    }

    // 레디스에 id가 있는지 조회
    public Set<Long> getId(String key) {
        Set<Object> idsObj = redisTemplate.opsForSet().members(key);
        Set<Long> ids = Objects.requireNonNull(idsObj).stream()
                .map(id -> Long.valueOf(String.valueOf(id)))
                .collect(Collectors.toSet());
        log.debug("ids 조회 {}", ids);
        return ids;
    }

    public void addToSet(String key, Long id) {
        // 키가 없다면 (Set이 없다면)
        if(!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            // Redis의 Set 자료구조를 이용해 주어진 communityId를 Set에 추가합니다.
            // redisTemplate.opsForValue().increment(key)를 사용하여 지정된 키의 값을 증분시킵니다.
            // 값이 없을 경우 Redis는 0부터 시작해 1을 더합니다.
            redisTemplate.opsForSet().add(key, String.valueOf(id));

            // 만료 기간 설정
            // 만료 기간은 TimeUtils 클래스에서 제공하는 시간을 이용해 자정까지의 초 단위 시간을 구해 설정
            redisTemplate.expire(key, TimeUtils.getRemainingTimeUntilMidnight(), TimeUnit.SECONDS);
            log.debug("Created new set for key {} with communityId {}", key, id);
        } else {
            // 기존 키 값으로 된 set에 추가
            redisTemplate.opsForSet().add(key, String.valueOf(id));
            log.debug("Add communityId {} to existing set for key {}", id, key);
        }
    }

    // 주어진 id가 레디스에 저장된 set에 있는지 확인하는 기능
    public boolean isExistInRedis(String key, Long id) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(id)));
    }

    // 레디스에 특정 키의 조회 수를 가져옵니다.
    public Integer getViewCount(String key) {
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        log.debug("Retrieved view count for key {}: {}", key, count);
        return count;
    }

    // 조회를 하면서 삭제
    public Integer getAndDeleteViewCount(String key) {
        // `opsForValue()`의 getAndDelete 메서드를 사용하여 조회수를 가져오면서 동시에 삭제
        Integer count = (Integer) redisTemplate.opsForValue().getAndDelete(key);
        log.debug("Retrieved and deleted view count for key {}: {}", key, count);
        return count;
    }

    // 레디스 삭제
    public void deleteCount(String key) {
        Boolean result = redisTemplate.delete(key);
        if(Boolean.TRUE.equals(result)) {
            log.debug("Deleted data for key {}", key);
        }
        log.warn("Failed to delete data for key {} or key does not exist", key);
    }

    // ZSet에서 랭킹 조회
    public List<Long> getTopRankedCommunityIds(int topCount) {
        log.debug("ZSet 랭킹 조회!!!!!");
        Set<Object> topIds = redisTemplate.opsForZSet()
                // reverseRange는 ZSet에서 점수(score)가 높은 순으로 정렬된 요소를 가져오는 명령어
                // 0 : 랭킹 1위 (가장 높은 조회수)
                // 	N위까지 가져옴 (예: topCount = 10이면 0~9 즉, 총 10개)
                .reverseRange("community:view:zset", 0, topCount - 1);

        if(topIds == null || topIds.isEmpty()) return List.of();

        return topIds.stream()
                .map(id -> Long.valueOf(String.valueOf(id)))
                .toList();
    }

}
