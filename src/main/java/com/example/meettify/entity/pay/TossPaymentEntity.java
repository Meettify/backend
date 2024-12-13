package com.example.meettify.entity.pay;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.dto.pay.RequestTossPaymentConfirmDTO;
import com.example.meettify.dto.pay.ResponseTossPaymentConfirmDTO;
import com.example.meettify.entity.member.AddressEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.pay.PayException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "toss_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class TossPaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toss_payment_id")
    private Long paymentId;
    @Column(nullable = false, length = 100)
    private String orderUid;
    private String status;
    private Long totalAmount;
    private String paymentKey;    // 결제 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    private LocalDateTime requestedAt;     // 승인시간
    @Schema(description = "결제 승인 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime approvedAt;      // 결제 승인 시간
    @Schema(description = "결제 방법")
    private String payMethod;

    public static TossPaymentEntity savePayment(ResponseTossPaymentConfirmDTO pay,
                                                MemberEntity member) {

        return TossPaymentEntity.builder()
                .payMethod(pay.getPayMethod())
                .orderUid(pay.getOrderUid())
                .member(member)
                .totalAmount(pay.getTotalAmount())
                .paymentKey(pay.getPaymentKey())
                .requestedAt(pay.getRequestedAt())
                .approvedAt(pay.getApprovedAt())
                .build();
    }
}
