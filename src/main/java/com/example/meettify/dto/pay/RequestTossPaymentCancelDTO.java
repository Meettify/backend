package com.example.meettify.dto.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestTossPaymentCancelDTO {
    @Schema(description = "토스 결재 고유 키")
    private String paymentKey;  // 결제 고유 키
    @Schema(description = "주문 ID")
    private String orderUid;     // 주문 ID
    @Schema(description = "결제 금액")
    private Long amount;        // 결제 금액
    @Schema(description = "취소 사유")
    private String cancelReason;  // Cancellation reason
}
