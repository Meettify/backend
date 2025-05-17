package com.example.meettify.config.redis.schedule;

import com.example.meettify.config.redis.RedisViewCountConfig;
import com.example.meettify.repository.jpa.community.CommunityRepository;
import com.example.meettify.repository.jpa.meetBoard.MeetBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/*
 *   worker : 유요한
 *   work   : 매일 정각에 레디스에서 조회수를 가져와서 디비에 쿼리를 보내주는 클라스
 *   date   : 2025/05/04
 * */
@EnableScheduling
@Component
@RequiredArgsConstructor
@Transactional
public class ViewCountSyncScheduler {
    private final RedisViewCountConfig redisViewCountConfig;
    private final CommunityRepository communityRepository;
    private final MeetBoardRepository meetBoardRepository;


    // 매일 자정에 커뮤니티 조회수 업데이트 실행
    // 1시간마다 실행 (매 시 0분)
    @Scheduled(cron = "0 0 * * * *")
    public void updateCommunityCount() {
        Set<Long> ids = redisViewCountConfig.getId("community:view:set");

        if(ids == null || ids.isEmpty()) return;

        for (Long id : ids) {
            Integer viewCount = redisViewCountConfig.getAndDeleteViewCount("community:view:" + id);

            if(viewCount != null) {
                communityRepository.incrementViewCount(id, viewCount);
            }
        }
        // 처리 끝나면 Set도 삭제
        redisViewCountConfig.deleteCount("community:view:set");
    }

    // 매일 자정에 모임 게시글 조회수 업데이트 실행
    // 1시간마다 실행 (매 시 0분)
    @Scheduled(cron = "0 0 * * * *")
    public void updateMeetBoardCount() {
        Set<Long> ids = redisViewCountConfig.getId("meetBoard:view:set");

        if(ids == null || ids.isEmpty()) return;

        for (Long id : ids) {
            Integer viewCount = redisViewCountConfig.getAndDeleteViewCount("meetBoard:view:" + id);

            if(viewCount != null) {
                meetBoardRepository.incrementViewCount(id, viewCount);
            }
        }
        // 처리 끝나면 Set도 삭제
        redisViewCountConfig.deleteCount("meetBoard:view:set");
    }
}
