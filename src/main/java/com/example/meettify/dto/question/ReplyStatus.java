package com.example.meettify.dto.question;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ReplyStatus {
    @Schema(description = "답글 완료")
    REPLY_O,
    @Schema(description = "답글 미완료")
    REPLY_X
}
