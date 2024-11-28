package com.example.meettify.service.notification;

import com.example.meettify.dto.notification.ResponseNotificationDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.notification.NotificationEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.community.CommunityRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.notification.CustomNotificationRepository;
import com.example.meettify.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
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
        log.info("eventId {} ", eventId);

        // SseEmitter 생성후 Map에 저장
        SseEmitter sseEmitter = customNotificationRepository.save(eventId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("sseEmitter {}", sseEmitter);


        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> {
            log.info("onCompletion callback");
            customNotificationRepository.deleteById(eventId);
        });

        // Emitter의 유효 시간이 만료되면 emitter 삭제
        // 유효 시간이 만료되었다는 것은 클라이언트와 서버가 연결된 시간동안 아무런 이벤트가 발생하지 않은 것을 의미한다.
        sseEmitter.onTimeout(() -> {
            log.info("onTimeout callback");
            customNotificationRepository.deleteById(eventId);
        });


        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(eventId, sseEmitter, "알림 서버 연결 성공 [memberId = " + findMember.getMemberId() + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, findMember.getMemberId(), sseEmitter);
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sseEmitter.send(SseEmitter.event().name("keepalive").data("ping"));
            } catch (IOException e) {
                customNotificationRepository.deleteById(eventId);
                scheduler.shutdownNow();
            }
        }, DUMMY_EVENT_INTERVAL, DUMMY_EVENT_INTERVAL, TimeUnit.MILLISECONDS);

        return  sseEmitter;
    }

    private static @NotNull String makeTimeIncludeId(MemberEntity findMember) {
        return findMember.getMemberId() + "_" + System.currentTimeMillis();
    }

    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .id(eventId)
                    .data(data));
        } catch (IOException e) {
            log.error("Failed to send SSE event: {}", e.getMessage());
            customNotificationRepository.deleteById(eventId);
            throw new RuntimeException("알림 서버 연결 오류");
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
        if(customNotificationRepository.containsKey(memberId)) {
            String eventId = makeTimeIncludeId(findMember);
            Map<String, SseEmitter> sseEmitterMap = customNotificationRepository.findAllEmitterStartWithByMemberId(memberId);
            log.info("sseEmitterMap {}", sseEmitterMap);
            // 8. 알림 메시지 전송 및 해제
            sseEmitterMap.forEach((id, emitter) -> {

                customNotificationRepository.saveEventCacheId(id, saveNotification);
                sendNotification(emitter, eventId, id, ResponseNotificationDTO.change(eventId, message, saveNotification));
            });
        }
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
    public void notifyCommentForCommunity(Long communityId, String message) {
        CommunityEntity findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));

        Long memberId = findCommunity.getMember().getMemberId();
        log.info("커뮤니티 작성자 Id: " + memberId);

        NotificationEntity saveNotification = notificationRepository.save(NotificationEntity.save(findCommunity.getMember(), message));

        if(customNotificationRepository.containsKey(memberId)) {
            String eventId = makeTimeIncludeId(findCommunity.getMember());
            Map<String, SseEmitter> sseEmitterMap = customNotificationRepository.findAllEmitterStartWithByMemberId(memberId);
            log.info("sseEmitterMap {}", sseEmitterMap);
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
        if(Boolean.TRUE.equals(findNotification.isRead())) {
            log.info("읽은 알람입니다.");
        }

        if(!findNotification.getMember().getMemberEmail().equals(email)) {
            throw new MemberException("해당 유저의 알림이 아닙니다.");
        }

        // 읽음 상태로 변경
        findNotification.changeRead();
        notificationRepository.save(findNotification);
        log.info("알림 ID {}가 읽음 처리되었습니다.", notificationId);
    }
}
