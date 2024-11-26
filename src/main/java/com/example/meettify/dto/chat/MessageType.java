package com.example.meettify.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;

public enum MessageType {
    @Schema(description = "방접속 상태")
    ENTER,
    @Schema(description = "채팅중인 상태")
    TALK
}
