package com.example.meettify.service.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.CancelPaymentDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.dto.pay.ResponsePaymentDTO;
import com.example.meettify.dto.pay.ResponseTossPaymentConfirmDTO;
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

    // 토스 결제 조회
    ResponseTossPaymentConfirmDTO getTossPaymentConfirm(String orderUid);

}
