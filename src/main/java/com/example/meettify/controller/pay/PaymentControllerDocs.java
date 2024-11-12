package com.example.meettify.controller.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.pay.CancelPaymentDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "결제", description = "결제 API")
public interface PaymentControllerDocs {
    @Operation(summary = "결제", description = "결제하는 API")
    IamportResponse<Payment> payForOrder(RequestPaymentDTO pay,
                                         AddressDTO address,
                                         List<RequestOrderDTO> orders,
                                         UserDetails userDetails) throws Exception;

    @Operation(summary = "결제 취소", description = "결제 취소 API")
    IamportResponse<Payment> cancelPayment(CancelPaymentDTO cancel);
}
