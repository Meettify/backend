package com.example.meettify.document.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.MessageType;
import com.example.meettify.dto.chat.SharePlaceDTO;
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
@Document(collection = "chat_message")
public class ChatMessage {

    @Id
    private String id;                  // 몽고 디비에서 자동으로 할당할 ID
    private MessageType type;           // 체팅 타입
    private Long roomId;              // 채팅방 번호
    private String message;             // 체팅
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime writeTime;    // 채팅 시간
    private String sender;
    private SharePlace place;

    public static ChatMessage create(ChatMessageDTO chat) {
        return ChatMessage.builder()
                .type(chat.getType())
                .roomId(chat.getRoomId())
                .message(chat.getMessage())
                .writeTime(chat.getWriteTime() != null ? chat.getWriteTime() : LocalDateTime.now()) // ✅ 보완
                .sender(chat.getSender())
                .place(chat.getPlace() != null ? changePlace(chat.getPlace()) : null) // ✅ null 체크
                .build();
    }

    public static SharePlace changePlace(SharePlaceDTO place) {
        return SharePlace.builder()
                .title(place.getTitle())
                .address(place.getAddress())
                .lat(place.getLat())
                .lng(place.getLng())
                .mapUrl(place.getMapUrl())
                .build();
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
