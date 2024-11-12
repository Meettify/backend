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

@Entity(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class PaymentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false, length = 100)
    private String payMethod;

    @Column(nullable = false, length = 100)
    private String impUid;

    @Column(nullable = false, length = 100)
    private String orderUid;

    @Column(nullable = false)
    private int amount;

    @Embedded
    private AddressEntity address;

    private int payPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public static PaymentEntity savePayment(RequestPaymentDTO pay,
                                            AddressDTO address,
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

        if (pay.getPayPrice() != iamportAmount) {
            throw new PayException("결제 금액과 서버 금액이 일치하지 않습니다.");
        }

        return PaymentEntity.builder()
                .payMethod(iamportPayMethod)
                .impUid(iamportImpUid)
                .orderUid(pay.getOrderUid())
                .amount(pay.getItemCount())
                .address(AddressEntity.changeEntity(address))
                .member(member)
                .payPrice(iamportAmount)
                .build();
    }
}
