package com.example.meettify.service.notification;

import com.example.meettify.dto.notification.ResponseNotificationDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.notification.NotificationEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.jpa.community.CommunityRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.notification.CustomNotificationRepository;
import com.example.meettify.repository.jpa.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    // SSE 이벤트 타임아웃 시간
    private static final Long DEFAULT_TIMEOUT = 24L * 60 * 60 * 1000;   // SSE 연결 타임아웃 (1일)
    private static final long DUMMY_EVENT_INTERVAL = 30000; // 30초 간격
    private final CustomNotificationRepository customNotificationRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final NotificationRepository notificationRepository;

    // 메시지 알림
    public SseEmitter subscribe(String memberEmail, String lastEventId) throws Exception {
        // 회원 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);

        // 매 연결마다 고유 이벤트 ID 부여
        String eventId = makeTimeIncludeId(findMember);
        log.debug("eventId {} ", eventId);

        // SseEmitter 생성후 Map에 저장
        SseEmitter sseEmitter = customNotificationRepository.save(eventId, new SseEmitter(DEFAULT_TIMEOUT));
        log.debug("sseEmitter {}", sseEmitter);

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, findMember.getMemberId(), sseEmitter);
        }

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(eventId, sseEmitter, "알림 서버 연결 성공 [memberId = " + findMember.getMemberId() + "]");

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> {
            log.debug("onCompletion callback");
            customNotificationRepository.deleteById(eventId);
        });

        // Emitter의 유효 시간이 만료되면 emitter 삭제
        // 유효 시간이 만료되었다는 것은 클라이언트와 서버가 연결된 시간동안 아무런 이벤트가 발생하지 않은 것을 의미한다.
        sseEmitter.onTimeout(() -> {
            log.debug("onTimeout callback");
            customNotificationRepository.deleteById(eventId);
        });


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sseEmitter.send(SseEmitter.event().name("keepalive").data("ping"));
            } catch (IOException e) {
                customNotificationRepository.deleteById(eventId);
                scheduler.shutdownNow();
            }
        }, 0, DUMMY_EVENT_INTERVAL, TimeUnit.MILLISECONDS);

        return sseEmitter;
    }

    private static @NotNull String makeTimeIncludeId(MemberEntity findMember) {
        return findMember.getMemberId() + "_" + System.currentTimeMillis();
    }

    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data) {
        try {
            // 데이터 캐시 저장 (유실된 데이터를 처리하기 위함)
            if(!customNotificationRepository.existsEventCache(eventId)) {
                customNotificationRepository.saveEventCacheId(eventId, data);
            }
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .id(eventId)
                    .data(data));

            // 성공적으로 전송되었다면 데이터 캐시 삭제, emitter는 계속 유지
            customNotificationRepository.deleteEventCache(eventId);
        } catch (Exception e) {
            // Broken pipe 예외 발생 시 로그만 남기고 삭제는 하지 않음
            if (e.getMessage().contains("Broken pipe")) {
                log.warn("[SSE ERROR] 클라이언트 연결 해제 (Broken pipe): {}", eventId);
            } else {
                log.warn("Failed to send SSE event: {}", e.getMessage());
            }
            sseEmitter.complete();  // SSE 연결 종료
            customNotificationRepository.deleteById(eventId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId,
                              Long memberId,
                              SseEmitter sseEmitter) {
        Map<String, Object> eventCaches = customNotificationRepository.findAllEventCacheStartWithByMemberId(memberId);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(entry.getKey(), sseEmitter, entry.getValue()));
    }


    // 채팅 수신 알림 - receiver에게
    public void notifyMessage(String receiver, String message) {

        // 수신자 정보 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(receiver);

        NotificationEntity saveNotification = notificationRepository.save(NotificationEntity.save(findMember, message));
        log.info("saveNotification {}", saveNotification);
        // 수신자 정보로부터 id 값 추출
        Long memberId = findMember.getMemberId();

        //  Map에서 memberId로 사용자 검색
        String eventId = makeTimeIncludeId(findMember);
        Map<String, SseEmitter> sseEmitterMap = customNotificationRepository.findAllEmitterStartWithByMemberId(memberId);
        log.debug("sseEmitterMap {}", sseEmitterMap);
        log.debug("Found SSE Emitters for memberId {}: {}", memberId, sseEmitterMap);

        // 8. 알림 메시지 전송 및 해제
        sseEmitterMap.forEach((id, emitter) -> {

            customNotificationRepository.saveEventCacheId(id, saveNotification);
            sendNotification(emitter, eventId, id, ResponseNotificationDTO.change(eventId, message, saveNotification));
        });
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException e) {
            customNotificationRepository.deleteById(emitterId);
        }
    }


    // 댓글 알림 - 게시글 작성자에게
    public void notifyCommentForCommunity(String email, Long communityId, String message) {
        CommunityEntity findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));

        Long memberId = findCommunity.getMember().getMemberId();
        log.debug("커뮤니티 작성자 Id: " + memberId);

        if (!findCommunity.getMember().getMemberEmail().equals(email)) {
            NotificationEntity saveNotification = notificationRepository.save(NotificationEntity.save(findCommunity.getMember(), message));

            String eventId = makeTimeIncludeId(findCommunity.getMember());
            Map<String, SseEmitter> sseEmitterMap = customNotificationRepository.findAllEmitterStartWithByMemberId(memberId);
            log.debug("sseEmitterMap {}", sseEmitterMap);
            // 8. 알림 메시지 전송 및 해제
            sseEmitterMap.forEach((id, emitter) -> {
                customNotificationRepository.saveEventCacheId(id, saveNotification);
                sendNotification(emitter, eventId, id, ResponseNotificationDTO.change(eventId, message, saveNotification));
            });
        }
    }

    // 알림 읽기
    public void readNotification(Long notificationId, String email) throws Exception {
        NotificationEntity findNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("삭제된 알람입니다."));
        if (Boolean.TRUE.equals(findNotification.isRead())) {
            log.debug("읽은 알람입니다.");
        }

        if (!findNotification.getMember().getMemberEmail().equals(email)) {
            throw new MemberException("해당 유저의 알림이 아닙니다.");
        }

        // 읽음 상태로 변경
        findNotification.changeRead();
        notificationRepository.save(findNotification);
        log.info("알림 ID {}가 읽음 처리되었습니다.", notificationId);
    }

    // 알림 삭제
    public void removeNotification(Long notificationId, String email) throws Exception {
        NotificationEntity findNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("삭제된 알람입니다."));

        notificationRepository.delete(findNotification);
        log.info("알림 ID {}가 삭제 처리되었습니다.", notificationId);
    }

    // 알림 리스트를 조회와서 레디스에 캐시처리
    @Cacheable(cacheNames = "getNotifications", key = "'notifications:email:' + #email", cacheManager = "cacheManager")
    public List<ResponseNotificationDTO> getAllNotifications(String email, int offset) {
        List<NotificationEntity> findAll = customNotificationRepository.findNotificationsWithLimitAndOffset(email, 10, offset);
        return findAll.stream().map(ResponseNotificationDTO::changeList)
                .toList();
    }
}
