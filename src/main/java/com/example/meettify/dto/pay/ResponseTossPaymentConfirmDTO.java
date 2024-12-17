package com.example.meettify.dto.pay;


import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.entity.pay.TossPaymentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseTossPaymentConfirmDTO {
    @Schema(description = "주문 ID")
    private String orderId;       // 주문 ID
    @Schema(description = "결제 금액")
    private Long amount;       // 결제 금액
    @Schema(description = "결제 고유 키")
    private String paymentKey;      // 결제 고유 키
    @Schema(description = "승인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime requestedAt;
    @Schema(description = "결제 승인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime approvedAt;
    @Schema(description = "주문 ID")
    private String orderUid;         // 주문 ID
    @Schema(description = "주소")
    private AddressDTO address;

    public static ResponseTossPaymentConfirmDTO change(TossPaymentEntity toss) {
        return ResponseTossPaymentConfirmDTO.builder()
                .paymentKey(toss.getPaymentKey())
                .orderUid(toss.getOrderUid())
                .amount(toss.getAmount())
                .requestedAt(toss.getRequestedAt())
                .approvedAt(toss.getApprovedAt())
                .build();
    }
}
