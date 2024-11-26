package com.example.meettify.dto.chat;

import com.example.meettify.document.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChatMessageDTO {
    @Schema(description = "채팅 상태")
    private MessageType type;
    @Schema(description = "채팅중인 방번호")
    private Long roomId;
    @Schema(description = "채팅 내용")
    private String message;
    @Schema(description = "채팅치는 사람")
    private String sender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH:mm")
    @Schema(description = "채팅 시간")
    private LocalDateTime writeTime;    // 채팅 시간

    public static ChatMessageDTO change(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getRoomId())
                .message(chatMessage.getMessage())
                .sender(chatMessage.getSender())
                .writeTime(chatMessage.getWriteTime())
                .build();
    }
}
