package com.example.meettify.dto.pay;

import com.example.meettify.entity.pay.PaymentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponsePaymentDTO {
    @Schema(description = "결제 번호")
    private Long paymentId;
    @Schema(description = "구매 개수")
    private int itemCount;
    @Schema(description = "결제 uid")
    private String impUid;
    @Schema(description = "주문 uid")
    private String orderUid;
    @Schema(description = "결제 방법")
    private String payMethod;
    @Schema(description = "결제 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime payTime;

    public static ResponsePaymentDTO changePayment(PaymentEntity pay) {
        return ResponsePaymentDTO.builder()
                .paymentId(pay.getPaymentId())
                .itemCount(pay.getCount())
                .impUid(pay.getImpUid())
                .orderUid(pay.getOrderUid())
                .payMethod(pay.getPayMethod())
                .payTime(pay.getRegTime())
                .build();
    }
}
