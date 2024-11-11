package com.example.meettify.dto.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestPaymentDTO {
    @Schema(description = "상품 번호")
    private Long itemId;
    @Schema(description = "구매 개수")
    private int itemCount;
}
