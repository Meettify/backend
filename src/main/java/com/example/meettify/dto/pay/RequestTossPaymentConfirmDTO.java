package com.example.meettify.dto.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RequestTossPaymentConfirmDTO {
    @Schema(description = "주문 ID")
    private String tossOrderId;       // 주문 ID
    @Schema(description = "결제 금액")
    private Long amount;          // 결제 금액
    @Schema(description = "토스 결제 키")
    private String paymentKey;    // 결제 키
    @Schema(description = "승인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")  // ISO 8601 형식
    private OffsetDateTime requestedAt;
    @Schema(description = "결제 승인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")  // ISO 8601 형식
    private OffsetDateTime approvedAt;
    @Schema(description = "주문 uid")
    private String orderUid;
    @Schema(description = "주문 정보")
    private List<RequestOrderDTO> orders;  // 주문 정보 (상세)
}
