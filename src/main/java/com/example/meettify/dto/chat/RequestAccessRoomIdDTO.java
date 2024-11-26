package com.example.meettify.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class RequestAccessRoomIdDTO {
    @Schema(description = "방 초대번호")
    private String roomInviteUid;

}
