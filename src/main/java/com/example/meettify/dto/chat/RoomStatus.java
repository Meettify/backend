package com.example.meettify.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

public enum RoomStatus {
    @Schema(description = "1:1 채팅")
    PERSONAL,
    @Schema(description = "단체 채팅")
    OPEN
}
