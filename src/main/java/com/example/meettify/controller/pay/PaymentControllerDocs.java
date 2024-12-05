package com.example.meettify.controller.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.*;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;


@Tag(name = "결제", description = "결제 API")
public interface PaymentControllerDocs {
    @Operation(summary = "아임포트 결제", description = "결제하는 API")
    IamportResponse<Payment> payForOrder(RequestPaymentDTO pay,
                                         UserDetails userDetails) throws Exception;

    @Operation(summary = "아임포트 결제 취소", description = "결제 취소 API")
    IamportResponse<Payment> cancelPayment(CancelPaymentDTO cancel);

    @Operation(summary = "아임포트 결제 조회", description = "아임포트 결제 조회 API")
    ResponseEntity<?> getImportPayInfo(String orderUid);

    @Operation(summary = "TOSS 결제", description = "TOSS 결제 API")
    ResponseEntity<?> confirmTossPayment(RequestTossPaymentConfirmDTO tossPay,
                                                     UserDetails userDetails);

    @Operation(summary = "TOSS 결제 취소", description = "TOSS 결제 취소 API")
    ResponseEntity<?> cancelTossPayment(RequestTossPaymentCancelDTO payment);

    @Operation(summary = "토스 결제 조회", description = "토스 결제 조회 API")
    ResponseEntity<?> getTossPayInfo(String orderUid);
}
