package com.example.meettify.entity.pay;

import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.pay.RequestTossPaymentConfirmDTO;
import com.example.meettify.dto.pay.ResponseTossPaymentConfirmDTO;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Entity(name = "toss_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class TossPaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toss_payment_id")
    private Long paymentId;
    @Column(nullable = false, name = "pay_amount")
    private Long amount;
    @Column(nullable = false, length = 100)
    private String orderUid;
    @Column(nullable = false, name = "order_id")
    private String orderId;
    private String paymentKey;    // 결제 키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;
    @Column(nullable = false)
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;

    public static TossPaymentEntity savePayment(RequestTossPaymentConfirmDTO pay,
                                                MemberEntity member) {


        return TossPaymentEntity.builder()
                .amount(pay.getAmount())
                .orderUid(pay.getOrderUid())
                .orderId(pay.getOrderId())
                .paymentKey(pay.getPaymentKey())
                .member(member)
                .requestedAt(pay.getRequestedAt())
                .approvedAt(pay.getApprovedAt())
                .build();
    }

    public static TossPaymentEntity savePayment(ResponseTossPaymentConfirmDTO pay,
                                                MemberEntity member) {

        return TossPaymentEntity.builder()
                .amount(pay.getAmount())
                .orderUid(pay.getOrderUid())
                .orderId(pay.getOrderId())
                .paymentKey(pay.getPaymentKey())
                .member(member)
                .requestedAt(pay.getRequestedAt())
                .approvedAt(pay.getApprovedAt())
                .build();
    }
}
