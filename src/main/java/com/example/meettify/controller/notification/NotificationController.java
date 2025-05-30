package com.example.meettify.controller.notification;

import com.example.meettify.dto.notification.ResponseNotificationDTO;
import com.example.meettify.exception.sse.SseException;
import com.example.meettify.service.notification.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@RestController
@RequestMapping("/api/v1/notify")
@RequiredArgsConstructor
@Slf4j
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    // 메시지 알림
    // SSE 통신을 위해서는 produces로 반환할 데이터 타입을 "text/event-stream"으로 해주어야 함
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestHeader(value = "last-event-id", required = false, defaultValue = "") final String lastEventId,
                                HttpServletResponse response) {
        try {

            if (userDetails == null) {
                throw new IllegalArgumentException("인증 정보가 필요합니다.");
            }

            response.setHeader("Connection", "keep-alive");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Accel-Buffering", "no");
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");

            String email = userDetails.getUsername();
            SseEmitter responseEmitter = notificationService.subscribe(email, lastEventId);

            log.info("Subscribed to email: " + email);
            log.info("response: " + responseEmitter);
            return responseEmitter;
        } catch (Exception e) {
            throw new SseException(e.getMessage());
        }
    }


    @GetMapping(path = "/send")
    public ResponseEntity<?> test() {
        String email = "zxzz010@naver.com";
        notificationService.notifyMessage(email, "SSE 알람 테스트 보내기");
        return ResponseEntity.ok().body("SSE 알람 테스트 보내기");
    }

    // 알림 삭제
    @Override
    @DeleteMapping(path = "/{notification-id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removeNotification(@PathVariable("notification-id") Long notificationId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            notificationService.removeNotification(notificationId, email);
            return ResponseEntity.ok().body("알림 삭제가 완료되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 알림 리스트
    @Override
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam int offset) {
        try {
            String email = userDetails.getUsername();
            List<ResponseNotificationDTO> response = notificationService.getAllNotifications(email, offset);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
