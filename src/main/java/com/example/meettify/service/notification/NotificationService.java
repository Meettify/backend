package com.example.meettify.service.notification;

import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.repository.community.CommunityRepository;
import com.example.meettify.repository.item.ItemRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.notification.CustomNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {
    // SSE 이벤트 타임아웃 시간
    private static final Long DEFAULT_TIMEOUT = -1L; // 무제한
    private final CustomNotificationRepository customNotificationRepository;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;

    // 메시지 알림
    public SseEmitter subscribe(String memberEmail) throws Exception {
        MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 2. 연결
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 저장
        // sse의 유효시간이 만료되면, 클라이언트에서 다시 서버로 이벤트 구독을 시도한다.
        customNotificationRepository.save(findMember.getMemberId(), sseEmitter);

        // 4. 연결 종료 처리
        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> customNotificationRepository.deleteById(findMember.getMemberId()));
        // Emitter의 유효 시간이 만료되면 emitter 삭제
        // 유효 시간이 만료되었다는 것은 클라이언트와 서버가 연결된 시간동안 아무런 이벤트가 발생하지 않은 것을 의미한다.
        sseEmitter.onTimeout(() -> customNotificationRepository.deleteById(findMember.getMemberId()));
        sseEmitter.onError((e) -> customNotificationRepository.deleteById(findMember.getMemberId()));

        return  sseEmitter;
    }

    // 채팅 수신 알림 - receiver에게
    public void notifyMessage(String receiver, String message) {
        // 5. 수신자 정보 조회
        MemberEntity findMember = memberRepository.findByMemberEmail(receiver);

        // 6. 수신자 정보로부터 id 값 추출
        Long memberId = findMember.getMemberId();

        // 7. Map에서 memberId로 사용자 검색
        if(customNotificationRepository.containsKey(memberId)) {
            SseEmitter sseEmitterReceiver = customNotificationRepository.findById(memberId);
            // 8. 알림 메시지 전송 및 해제
            try {
                sseEmitterReceiver.send(SseEmitter.event().name("addMessage").data(message));
            } catch (Exception e) {
                customNotificationRepository.deleteById(memberId);
            }
        }
    }

    // 댓글 알림 - 게시글 작성자에게
    public void notifyCommentForCommunity(Long communityId) {
        CommunityEntity findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new BoardException("커뮤니티 글이 없습니다."));

        Long memberId = findCommunity.getMember().getMemberId();
        log.info("커뮤니티 작성자 Id: " + memberId);

        if(customNotificationRepository.containsKey(memberId)) {
            SseEmitter sseEmitter = customNotificationRepository.findById(memberId);
            try {
                sseEmitter.send(SseEmitter.event().name("addComment").data("댓글이 달렸습니다."));
            } catch (Exception e) {
                customNotificationRepository.deleteById(memberId);
            }
        }
    }

    // 상품 등록 후 관리자에게 알림을 전송하는 메서드
    public void notifyNewItemCreated(String itemName) {
        String message = "새로운 상품이 등록되었습니다: " + itemName;
        // 1. 관리자 역할을 가진 모든 사용자 조회
        List<MemberEntity> allAdmin = memberRepository.findAllByMemberRole(UserRole.ADMIN);

        // 2. 각 관리자에게 알림 전송
        for (MemberEntity admin : allAdmin) {
            Long adminId = admin.getMemberId();

            if(customNotificationRepository.containsKey(adminId)){
                SseEmitter sseEmitter = customNotificationRepository.findById(adminId);
                try {
                    sseEmitter.send(SseEmitter.event().name("newItem").data(message));
                } catch (Exception e) {
                    customNotificationRepository.deleteById(adminId);
                    log.error("Error sending notification to admin: " + admin.getMemberEmail(), e);
                }
            }
        }
    }
}
