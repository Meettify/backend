package com.example.meettify.dto.chat;

import com.example.meettify.entity.chat_room.ChatRoomEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.ElementCollection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class ChatRoomDTO{
    @Schema(description = "방 번호")
    private Long roomId;
    @Schema(description = "방 제목")
    private String roomName;
    @Schema(description = "방 초대번호")
    private String roomInviteUid;
    @Schema(description = "방 초대 유저번호")
    private List<Long> inviteMemberIds = new ArrayList<>();
    @Schema(description = "방 생성자 닉네임")
    private String createdNickName;

    public static ChatRoomDTO change(ChatRoomEntity chat) {
        return ChatRoomDTO.builder()
                .roomId(chat.getRoomId())
                .roomName(chat.getRoomName())
                .roomInviteUid(chat.getRoomInviteUid())
                .createdNickName(chat.getCreatedNickName())
                .build();
    }
}
