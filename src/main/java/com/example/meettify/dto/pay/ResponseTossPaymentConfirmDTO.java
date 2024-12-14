package com.example.meettify.dto.pay;


import com.example.meettify.entity.pay.TossPaymentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseTossPaymentConfirmDTO {
    @Schema(description = "결제 고유 키")
    private String paymentKey;      // 결제 고유 키
    @Schema(description = "주문 ID")
    private String orderUid;         // 주문 ID
    @Schema(description = "결제 상태")
    private String status;          // 결제 상태 ("DONE", "CANCELED" 등)
    @Schema(description = "결제 금액")
    private Long totalAmount;       // 결제 금액
    @Schema(description = "승인 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private LocalDateTime requestedAt;     // 승인시간
    @Schema(description = "결제 승인 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime approvedAt;      // 결제 승인 시간
    @Schema(description = "결제 방법")
    private String payMethod;
    @Schema(description = "주문한 상품 이름들")
    private List<String> orders;

    public static ResponseTossPaymentConfirmDTO change(TossPaymentEntity toss) {
        return ResponseTossPaymentConfirmDTO.builder()
                .paymentKey(toss.getPaymentKey())
                .orderUid(toss.getOrderUid())
                .status(toss.getStatus())
                .totalAmount(toss.getTotalAmount())
                .requestedAt(toss.getRequestedAt())
                .approvedAt(toss.getApprovedAt())
                .payMethod(toss.getPayMethod())
                .orders(toss.getOrders())
                .build();
    }
}
