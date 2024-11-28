package com.example.meettify.entity.chat_room;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.chat.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ChatRoomEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;
    private String createdNickName;
    @ElementCollection
    private List<Long> inviteMemberIds = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    public static ChatRoomEntity create(String roomName,
                                        String createdNickName,
                                        RoomStatus roomStatus) {

        return ChatRoomEntity.builder()
                .roomName(roomName)
                .createdNickName(createdNickName)
                .roomStatus(roomStatus)
                .build();
    }
}
