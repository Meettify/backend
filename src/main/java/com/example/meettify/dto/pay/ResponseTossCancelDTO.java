package com.example.meettify.dto.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTossCancelDTO {
    @Schema(description = "토스 결제 고유 키")
    private String paymentKey;
    @Schema(description = "주문 ID")
    private String orderId;
    @Schema(description = "주문 이름")
    private String orderName;
    @Schema(description = "결제 상태")
    private String status;

}
