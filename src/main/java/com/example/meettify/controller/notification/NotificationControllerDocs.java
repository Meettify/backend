package com.example.meettify.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림", description = "알림 API")
public interface NotificationControllerDocs {
    @Operation(summary = "알림 구독", description = "알림 구독하는 API")
    SseEmitter subscribe(UserDetails userDetails, final String lastEventId, HttpServletResponse response);
    @Operation(summary = "알림 읽기", description = "알림 읽는 API")
    ResponseEntity<?> readNotification(Long notificationId, UserDetails userDetails);
    @Operation(summary = "알림 삭제", description = "알림 삭제 API")
    ResponseEntity<?> removeNotification(Long notificationId, UserDetails userDetails);
    @Operation(summary = "알림 리스트", description = "알림 리스트 API")
    ResponseEntity<?> getNotifications(UserDetails userDetails, int offset);
}
