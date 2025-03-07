package com.example.meettify.service.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.*;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface PaymentService {
    // 결제하기
    ResponsePaymentDTO savePayment(RequestPaymentDTO pay,
                                   String email,
                                   IamportResponse<Payment> iamport);

    // 결제 취소
    IamportResponse<Payment> cancelPayment(CancelPaymentDTO cancel);

    // 아임포트 결제 조회
    ResponsePaymentDTO getPayment(String orderUid);

    // 토스 결제하기
    ResponseTossPaymentConfirmDTO savePayment(RequestTossPaymentConfirmDTO toss,
                                              String email);

    // 토스 결제 조회
    ResponseTossPaymentConfirmDTO getTossPaymentConfirm(String orderUid);
}
