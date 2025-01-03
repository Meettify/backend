package com.example.meettify.document.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Document(collation = "chat_message")
public class ChatMessage {

    @Id
    private String id;                  // 몽고 디비에서 자동으로 할당할 ID
    private MessageType type;           // 체팅 타입
    private Long roomId;              // 채팅방 번호
    private String message;             // 체팅
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH:mm")
    private LocalDateTime writeTime;    // 채팅 시간
    private String sender;

    public static ChatMessage create(ChatMessageDTO chat) {
        return ChatMessage.builder()
                .type(MessageType.TALK)
                .roomId(chat.getRoomId())
                .message(chat.getMessage())
                .writeTime(chat.getWriteTime())
                .sender(chat.getSender())
                .build();
    }
}
