package com.example.meettify.controller.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "결제", description = "결제 API")
public interface PaymentControllerDocs {
    @Operation(summary = "결제", description = "결제하는 API")
    public IamportResponse<Payment> payForOrder(@PathVariable String impUid,
                                                @RequestBody List<RequestPaymentDTO> orders,
                                                String email,
                                                AddressDTO address,
                                                String orderUUid) throws Exception;
}
