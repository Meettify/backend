package com.example.meettify.service.search;

import com.example.meettify.dto.search.DeleteSearchLog;
import com.example.meettify.dto.search.RequestSearchLog;
import com.example.meettify.dto.search.SearchLog;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SearchLogService {
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, SearchLog> redisTemplate;

    public void saveRecentSearchLog(String email, RequestSearchLog searchLog) {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);

        String now = LocalDateTime.now().toString();
        // key로 현재 로그인한 SearchLog + [현재 로그인한 member의 id 값] 으로 두었고
        String key = "SearchLog" + findMember.getMemberId();
        SearchLog value = SearchLog.builder()
                .name(searchLog.getName())
                .createdAt(now)
                .build();

        log.info("Search Log {}", value);

        Long size = redisTemplate.opsForList().size(key);
        log.info("size: {}", size);

        // 만약 redis의 현재 크기가 10인 경우 rightTop을 통해 가장 오래된 데이터를 삭제해준다.
        // 10 미만이라면 leftPush를 통해 새로운 검색어를 추가해준다.
        if (size == 10) {
            // rightPop을 통해 가장 오래된 데이터 삭제
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, value);
    }

    // 최근 검색 기록 조회 로직
    public List<SearchLog> findRecentSearchLogs(String email) {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);

        String key = "SearchLog" + findMember.getMemberId();
        // Redis 리스트에서 key에 해당하는 값들 중 인덱스 0부터 10까지의 값을 가져온 후 반환해준다.
        List<SearchLog> logs = redisTemplate.opsForList()
                .range(key, 0, 10);
        log.info("logs: {}", logs);
        return logs;
    }

    // 검색 기록 삭제 로직
    public void deleteRecentSearchLog(String email, DeleteSearchLog searchLog) throws Exception {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);

        // key로 현재 로그인한 SearchLog + [현재 로그인한 member의 id 값] 으로 두었고
        String key = "SearchLog" + findMember.getMemberId();
        SearchLog value = SearchLog.builder()
                .name(searchLog.getName())
                .createdAt(searchLog.getCreatedAt())
                .build();

        // 삭제
        long count = redisTemplate.opsForList().remove(key, 1, value);
        if(count == 0) {
            throw new Exception("삭제할 수 없습니다.");
        }


    }
}
