package com.example.meettify.dto.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RequestTossPaymentConfirmDTO {
    @Schema(description = "주문 ID")
    private String orderId;       // 주문 ID
    @Schema(description = "결제 금액")
    private Long amount;          // 결제 금액
    @Schema(description = "토스 결제 키")
    private String paymentKey;    // 결제 키
    @Schema(description = "승인 시간")
    private LocalDateTime requestedAt;     // 승인시간
    @Schema(description = "결제 승인 시간")
    private LocalDateTime approvedAt;      // 결제 승인 시간
    @Schema(description = "주문 uid")
    private String orderUid;
    @Schema(description = "주문 정보")
    private List<RequestOrderDTO> orders;
    @Schema(description = "주소")
    private AddressDTO address;
}
