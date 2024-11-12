package com.example.meettify.controller.pay;

import com.example.meettify.config.iamport.ImportConfig;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.dto.pay.CancelPaymentDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.exception.pay.PayException;
import com.example.meettify.service.order.OrderService;
import com.example.meettify.service.pay.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Log4j2
public class PaymentController implements PaymentControllerDocs {
    private final ImportConfig importConfig;
    private final OrderService orderService;
    private final PaymentService paymentService;


    // 결제 검증
    @GetMapping("/verify")
    @PreAuthorize("hasRole('ROLE_USER')")
    public IamportResponse<Payment> payForOrder(@RequestBody RequestPaymentDTO pay,
                                                @RequestBody AddressDTO address,
                                                @RequestBody List<RequestOrderDTO> orders,
                                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            log.info("결제 검증 서비스 실행");
            String email = userDetails.getUsername();
            IamportResponse<Payment> paymentIamportResponse = importConfig.iamportClient().paymentByImpUid(pay.getImpUid());

            if (paymentIamportResponse.getResponse().getStatus().equals("paid")) {
                // 결제 성공 시 주문 정보 저장
                ResponseOrderDTO response = orderService.saveOrder(orders, email, address, pay.getOrderUid());
                log.info(response);

                return paymentIamportResponse;
            } else {
                throw new OrderException("결제 검증 실패 : ");
            }
        } catch (Exception e) {
            throw new Exception("결제 검증 중 오류 발생: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public IamportResponse<Payment> cancelPayment(@RequestBody CancelPaymentDTO cancel) {
        try {
            IamportResponse<Payment> response = paymentService.cancelPayment(cancel);
            log.info(response);
            return response;
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }
}