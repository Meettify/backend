package com.example.meettify.service.search;

import com.example.meettify.dto.search.DeleteSearchLog;
import com.example.meettify.dto.search.RequestSearchLog;
import com.example.meettify.dto.search.SearchLog;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSearchLogService {
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, SearchLog> redisTemplate;

    public void saveRecentSearchLog(String email, RequestSearchLog searchLog) {
        // 1. 회원 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        if (findMember == null) {
            throw new MemberException("Member not found");
        }


        // 2. 레디스 key 구성 : 현재 로그인한 SearchLog + [현재 로그인한 member의 id 값] 으로 두었고
        String key = "search:log:" + findMember.getMemberEmail();
        SearchLog value = SearchLog.builder()
                .name(searchLog.getName())
                // 검색을 레디스에 저장할 때 상품을 조회하고 그 카테고리를 레디스에 저장
                .createdAt(LocalDateTime.now().toString())
                .build();

        log.debug("Search Log {}", value);

        // 3. 현재 검색어 목록의 크기 확인
        Long size = Objects.requireNonNullElse(redisTemplate.opsForList().size(key), 0L);
        log.debug("size: {}", size);
        // 4. 레디스에서 현재 검색어 리스트 조회
        List<SearchLog> currentLogs = redisTemplate.opsForList().range(key, 0, size);

        // 5. 동일한 검색어가 존재하는지 확인
        boolean exists = currentLogs != null &&
                currentLogs.stream().anyMatch(log -> log.getName().equals(searchLog.getName()));

        // 6. 중복 검색어가 있다면 기존 값 삭제
        if(exists) {
            currentLogs.stream()
                    .filter(log -> log.getName().equals(searchLog.getName()))
                    .findFirst()
                    .ifPresent(existLog -> redisTemplate.opsForList().remove(key, 1, existLog));
        }

        // 7. 만약 redis의 현재 크기가 10인 경우 rightTop을 통해 가장 오래된 데이터를 삭제해준다.
        // 10 미만이라면 leftPush를 통해 새로운 검색어를 추가해준다.
        if (size >= 10) {
            // rightPop을 통해 가장 오래된 데이터 삭제
            redisTemplate.opsForList().rightPop(key);
        }

        // 8. 가장 최근 검색어로 추가
        redisTemplate.opsForList().leftPush(key, value);

        // 9. 레디스 키 만료시간 설정 (7일 후 자동 삭제)
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    // 최근 검색 기록 조회 로직
    public List<SearchLog> findRecentSearchLogs(String email) {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        if (findMember == null) {
            throw new MemberException("Member not found");
        }

        String key = "search:log:" + findMember.getMemberEmail();
        // Redis 리스트에서 key에 해당하는 값들 중 인덱스 0부터 10까지의 값을 가져온 후 반환해준다.
        List<SearchLog> logs = redisTemplate.opsForList()
                .range(key, 0, 10);
        log.debug("logs: {}", logs);
        return logs;
    }

    // 검색 기록 삭제 로직
    public void deleteRecentSearchLog(String email, DeleteSearchLog searchLog) throws Exception {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        if (findMember == null) {
            throw new MemberException("Member not found");
        }

        // key로 현재 로그인한 SearchLog + [현재 로그인한 member의 id 값] 으로 두었고
        String key = "search:log:" + findMember.getMemberEmail();
        SearchLog value = SearchLog.builder()
                .name(searchLog.getName())
                .build();

        // 삭제
        long count = Objects.requireNonNullElse(redisTemplate.opsForList().remove(key, 1, value), 0L);
        if (count == 0) {
            throw new MemberException("Unable to delete search log");
        }
    }
}
