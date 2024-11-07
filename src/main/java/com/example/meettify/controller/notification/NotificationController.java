package com.example.meettify.controller.notification;

import com.example.meettify.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Log4j2
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    // 메시지 알림
    @GetMapping("/subscribe")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        try {

            if (userDetails == null) {
                throw new IllegalArgumentException("인증 정보가 필요합니다.");
            }

            String email = userDetails.getUsername();
            SseEmitter response = notificationService.subscribe(email);

            log.info("Subscribed to email: " + email);
            log.info("response: " + response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
