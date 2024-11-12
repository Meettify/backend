package com.example.meettify.service.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.CancelPaymentDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.dto.pay.ResponsePaymentDTO;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

public interface PaymentService {
    ResponsePaymentDTO savePayment(RequestPaymentDTO pay,
                                   String email,
                                   AddressDTO address,
                                   IamportResponse<Payment> iamport);

    IamportResponse<Payment> cancelPayment(CancelPaymentDTO cancel);
}
