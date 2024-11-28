package com.example.meettify.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

public enum PayStatus {
    @Schema(description = "결제 완료")
    PAY_O,
    @Schema(description = "결제 취소")
    PAY_X
}
