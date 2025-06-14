package com.example.meettify.dto.pay;


import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.entity.pay.TossPaymentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResponseTossPaymentConfirmDTO {
    @Schema(description = "토스에서 반환해주는 주문 ID")
    @JsonProperty("orderId")
    private String tossOrderId;       // 주문 ID
    @Schema(description = "결제 금액")
    @JsonProperty("amount")
    private Long amount;       // 결제 금액
    @Schema(description = "결제 고유 키")
    @JsonProperty("paymentKey")
    private String paymentKey;      // 결제 고유 키
    @Schema(description = "주소")
    private AddressDTO address;
    @Schema(description = "주문 ID")
    @JsonProperty("orderId")
    private String orderUid;

    // 엔티티를 DTO로 변환
    public static ResponseTossPaymentConfirmDTO change(TossPaymentEntity toss) {
        return ResponseTossPaymentConfirmDTO.builder()
                .tossOrderId(toss.getTossOrderId())
                .paymentKey(toss.getPaymentKey())
                .amount(toss.getAmount())
                .orderUid(toss.getOrderUid())
                .build();
    }
}
