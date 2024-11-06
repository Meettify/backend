package com.example.meettify.service.community;

import com.example.meettify.config.cookie.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/*
 *   worker : 유요한
 *   work   : RedisService 클래스는 Redis와 통신하여 데이터를 저장하고 조작하는 여러 기능을 제공하는 서비스 계층
 *   date   : 2024/10/20
 * */
@RequiredArgsConstructor
@Service
@Log4j2
public class RedisCommunityService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis에 저장된 특정 키의 값을 증가시키는 기능을 수행합니다
    // key에 해당하는 값(정수형)을 1만큼 증가시키는 것
    public void increaseData(String key) {
        Long incrementedValue = redisTemplate
                .opsForValue()
                .increment(key);
        log.info("Increased value for key {}: {}", key, incrementedValue);
    }

    public void addToSet(String key, Long communityId) {
        // 키가 없다면 (set이 없다면)
        if(!redisTemplate.hasKey(key)) {
            // Redis의 Set 자료구조를 이용해 주어진 communityId를 Set에 추가합니다.
            // redisTemplate.opsForValue().increment(key)를 사용하여 지정된 키의 값을 증분시킵니다.
            // 값이 없을 경우 Redis는 0부터 시작해 1을 더합니다.
            redisTemplate
                    .opsForSet()
                    .add(key, String.valueOf(communityId));
            // 만료 기간 설정
            // 만료 기간은 TimeUtils 클래스에서 제공하는 시간을 이용해 자정까지의 초 단위 시간을 구해 설정
            redisTemplate.expire(key, TimeUtils.getRemainingTimeUntilMidnight(), TimeUnit.SECONDS);
            log.info("Created new set for key {} with communityId {}", key, communityId);
        } else  {
            // 기존 키 값으로 된 set에 추가
            redisTemplate.opsForSet().add(key, String.valueOf(communityId));
            log.info("Added communityId {} to existing set for key {}", communityId, key);
        }
    }
    // 주어진 communityId가 Redis에 저장된 Set 안에 있는지 확인하는 기능을 제공
    public boolean isExistInSet(String key, Long communityId) {
        return Boolean.TRUE
                .equals(redisTemplate.opsForSet().isMember(key, String.valueOf(communityId)));
    }

    // Redis에서 특정 키의 조회 수를 가져옵니다
    public Integer getViewCount(String key) {
        Integer value = (Integer) redisTemplate.opsForValue().get(key);
        log.info("Retrieved view count for key {}: {}", key, value);
        return value;
    }

    // 조회를 하면서 삭제
    public Integer getAndDeleteViewCount(String key) {
        // `opsForValue()`의 getAndDelete 메서드를 사용하여 조회수를 가져오면서 동시에 삭제
        Integer value = (Integer) redisTemplate.opsForValue().getAndDelete(key);
        log.info("Retrieved and deleted view count for key {}: {}", key, value);
        return value;
    }


    // 레디스 삭제
    public void deleteData(String key) {
        Boolean result = redisTemplate.delete(key);
        if(Boolean.TRUE.equals(result)) {
            log.info("Deleted data for key {}", key);
        }
        log.warn("Failed to delete data for key {} or key does not exist", key);
    }

}
