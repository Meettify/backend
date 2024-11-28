package com.example.meettify.entity.pay;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.entity.member.AddressEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.pay.PayException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity(name = "iamport_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class PaymentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iamport_payment_id")
    private Long paymentId;

    @Column(nullable = false, length = 100)
    private String payMethod;

    @Column(nullable = false, length = 100)
    private String impUid;

    @Column(nullable = false, length = 100)
    private String orderUid;

    @Column(nullable = false)
    private int count;

    private int payPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public static PaymentEntity savePayment(RequestPaymentDTO pay,
                                            MemberEntity member,
                                            IamportResponse<Payment> iamport) {

        String iamportPayMethod = iamport.getResponse().getPayMethod();
        String iamportImpUid = iamport.getResponse().getImpUid();
        int iamportAmount = iamport.getResponse().getAmount().intValue();

        if (!pay.getPayMethod().equals(iamportPayMethod)) {
            throw new PayException("결제 방법이 일치하지 않습니다.");
        }

        if (!pay.getImpUid().equals(iamportImpUid)) {
            throw new PayException("ImpUid가 일치하지 않습니다.");
        }

        // 금액 비교에서 BigDecimal을 사용하는 것이 금융 데이터 처리에서 더 안전하며,
        // 직접 int로 변환하지 않고 compareTo 메서드를 사용하는 것이 좋습니다.
        if (iamport.getResponse().getAmount().compareTo(BigDecimal.valueOf(pay.getPayPrice())) != 0) {
            throw new PayException("결제 금액과 서버 금액과 맞지 않습니다.");
        }

        return PaymentEntity.builder()
                .payMethod(iamportPayMethod)
                .impUid(iamportImpUid)
                .orderUid(pay.getOrderUid())
                .count(pay.getItemCount())
                .member(member)
                .payPrice(iamportAmount)
                .build();
    }

}
