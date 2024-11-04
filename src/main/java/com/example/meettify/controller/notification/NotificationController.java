package com.example.meettify.controller.notification;

import com.example.meettify.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/members/notification")
@RequiredArgsConstructor
@Log4j2
public class NotificationController implements NotificationControllerDocs {
    private final NotificationService notificationService;

    // 메시지 알림
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails) {
        try {
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
