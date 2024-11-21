package com.example.meettify.dto.pay;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTossPaymentConfirmDTO {
    @Schema(description = "결제 고유 키")
    private String paymentKey;      // 결제 고유 키
    @Schema(description = "주문 ID")
    private String orderId;         // 주문 ID
    @Schema(description = "결제 상태")
    private String status;          // 결제 상태 ("DONE", "CANCELED" 등)
    @Schema(description = "결제 금액")
    private Long totalAmount;       // 결제 금액
    @Schema(description = "주문 이름")
    private String orderName;       // 주문이름
    @Schema(description = "승인 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private LocalDateTime requestedAt;     // 승인시간
    @Schema(description = "결제 승인 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime approvedAt;      // 결제 승인 시간
}
