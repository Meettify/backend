package com.example.meettify.service.community;

import com.example.meettify.config.redis.RedisService;
import com.example.meettify.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 *   worker : 유요한
 *   work   : 매일 정각에 레디스에서 조회수를 가져와서 디비에 쿼리를 보내주는 클라스
 *   date   : 2024/10/20
 * */
@EnableScheduling
@Component
@RequiredArgsConstructor
@Transactional
public class ScheduledTasks {
    private final RedisService redisService;
    private final CommunityRepository communityRepository;

    @Scheduled(fixedRate = 86400000) // 매일 자정에 실행
    public void updateViewCountToDB() {
        // 모든 게시글 ID를 가져옵니다.
        List<Long> communityIds = communityRepository.findAllCommunityIds();
        for (Long communityId : communityIds) {
            // Redis에서 조회 수 가져오기
            Integer redisViewCount = redisService.getViewCount("viewCount_community" + communityId);
            if (redisViewCount != null) {
                // 데이터베이스에 업데이트
                communityRepository.incrementViewCount(communityId, redisViewCount);
                // Redis 조회 수 초기화
                redisService.resetViewCount("viewCount_community" + communityId);
            }
        }
    }
}
