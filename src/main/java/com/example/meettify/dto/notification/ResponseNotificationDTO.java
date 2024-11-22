package com.example.meettify.dto.notification;

import com.example.meettify.entity.notification.NotificationEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseNotificationDTO {
    private String eventId;
    private String message;
    private boolean isRead;
    private String url;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdBy;
    private Long notificationId;


    public static ResponseNotificationDTO change(String eventId,
                                                 String message,
                                                 NotificationEntity notification) {
        return ResponseNotificationDTO.builder()
                .eventId(eventId)
                .message(message)
                .createdBy(notification.getRegTime())
                .notificationId(notification.getId())
                .build();
    }
}
