package com.example.meettify.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림", description = "알림 API")
public interface NotificationControllerDocs {
    @Operation(summary = "알림 구독", description = "알림 구독하는 API")
    SseEmitter subscribe(UserDetails userDetails, final String lastEventId, HttpServletResponse response);
}
