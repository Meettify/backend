package com.example.meettify.dto.chat;

import com.example.meettify.document.chat.ChatMessage;
import com.example.meettify.document.chat.SharePlace;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "채팅 시간")
    private LocalDateTime writeTime;    // 채팅 시간

    @Schema(description = "공유한 장소 정보") // ✅ 새로 추가!
    private SharePlaceDTO place;

    public static ChatMessageDTO change(ChatMessage chatMessage) {
        if (chatMessage == null) return null;

        return ChatMessageDTO.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getRoomId())
                .message(chatMessage.getMessage() != null ? chatMessage.getMessage() : "")
                .sender(chatMessage.getSender() != null ? chatMessage.getSender() : "익명")
                .writeTime(chatMessage.getWriteTime() != null ? chatMessage.getWriteTime() : LocalDateTime.now())
                .place(changePlace(chatMessage.getPlace())) // MongoDB에 저장된 place 정보도 같이 매핑
                .build();
    }

    public static SharePlaceDTO changePlace(SharePlace place) {
        return SharePlaceDTO.builder()
                .title(place.getTitle())
                .address(place.getAddress())
                .lat(place.getLat())
                .lng(place.getLng())
                .mapUrl(place.getMapUrl())
                .build();
    }
}
