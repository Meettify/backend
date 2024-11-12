package com.example.meettify.dto.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestPaymentDTO {
    @Schema(description = "구매 개수")
    private int itemCount;
    @Schema(description = "결제 uid")
    private String impUid;
    @Schema(description = "주문 uid")
    private String orderUid;
    @Schema(description = "결제 방법")
    private String payMethod;
    @Schema(description = "결제 금액")
    private int payPrice;
}
