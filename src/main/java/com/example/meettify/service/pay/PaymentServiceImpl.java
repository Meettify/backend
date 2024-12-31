package com.example.meettify.service.pay;

import com.example.meettify.config.iamport.ImportConfig;
import com.example.meettify.dto.pay.*;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.pay.PaymentEntity;
import com.example.meettify.entity.pay.TossPaymentEntity;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.exception.pay.PayException;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.pay.PaymentRepository;
import com.example.meettify.repository.pay.TossPaymentRepository;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ImportConfig importConfig;
    private final TossPaymentRepository tossPaymentRepository;

    // 아임포트 결제 정보 저장
    public ResponsePaymentDTO savePayment(RequestPaymentDTO pay,
                                          String email,
                                          IamportResponse<Payment> iamport) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            if (findMember == null) {
                throw new MemberException("로그인을 해야 결제할 수 있습니다.");
            }

            // 실제로 결제된 금액과 아임포트 서버쪽 결제내역 금액과 같은지 확인
            // 이때 가격은 BigDecimal이란 데이터 타입으로 주로 금융쪽에서 정확한 값표현을 위해씀.
            // int형으로 비교해주기 위해 형변환 필요.
            if(iamport.getResponse().getAmount().intValue() != pay.getPayPrice()) {
                throw new PayException("결제 금액과 서버 금액과 맞지않습니다.");
            }

            // 결제 정보 엔티티 생성
            PaymentEntity paymentEntity = PaymentEntity.savePayment(pay, findMember, iamport);
            // 디비 저장
            PaymentEntity savePayment = paymentRepository.save(paymentEntity);
            return ResponsePaymentDTO.changePayment(savePayment);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new PayException("결제 정보를 저장하는데 실패했습니다.");
        }
    }


    // 아임포트 결제 취소
    public IamportResponse<Payment> cancelPayment(CancelPaymentDTO cancel) {
        try {
            CancelData cancelData = new CancelData(cancel.getImpUid(), true);
            IamportResponse<Payment> payment = importConfig.iamportClient().cancelPaymentByImpUid(cancelData);
            log.info("payment: {}", payment);
            return payment;
        } catch (Exception e) {
            throw new PayException("결제 취소하는데 실패했습니다.");
        }
    }

    // 아임포트 결제 정보 조회
    @Override
    public ResponsePaymentDTO getPayment(String orderUid) {
        try {
            // 아임포트 결제 정보 조회
            PaymentEntity findPayInfo = paymentRepository.findByOrderUid(orderUid);
            return ResponsePaymentDTO.changePayment(findPayInfo);
        } catch (Exception e) {
            throw new PayException("결제 정보를 가져오는데 실패했습니다.");
        }
    }

    // 토스 결제 정보 조회
    @Override
    public ResponseTossPaymentConfirmDTO getTossPaymentConfirm(String orderUid) {
        try {
            TossPaymentEntity findTossPay = tossPaymentRepository.findByOrderUid(orderUid);
            return ResponseTossPaymentConfirmDTO.change(findTossPay);
        } catch (Exception e) {
            throw new PayException("결제 정보를 가져오는데 실패했습니다.");
        }
    }

}
