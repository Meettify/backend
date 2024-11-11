package com.example.meettify.controller.pay;

import com.example.meettify.config.iamport.ImportConfig;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.dto.pay.RequestPaymentDTO;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.service.order.OrderService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class PaymentController implements PaymentControllerDocs {
    private final ImportConfig importConfig;
    private final OrderService orderService;
    private final HttpSession session;

    @GetMapping("/verify/{imp-uid}")
    public IamportResponse<Payment> payForOrder(@PathVariable String impUid,
                                                @RequestBody List<RequestPaymentDTO> orders,
                                                @RequestParam String email,
                                                @RequestBody AddressDTO address,
                                                @RequestParam String orderUUid) throws Exception {
        try {
            log.info("결제 검증 서비스 실행");
            IamportResponse<Payment> paymentIamportResponse = importConfig.iamportClient().paymentByImpUid(impUid);

            if (paymentIamportResponse.getResponse().getStatus().equals("paid")) {
                // 결제 성공 시 주문 정보 저장
                ResponseOrderDTO response = orderService.saveOrder(orders, email, address, orderUUid);
                log.info(response);
                return paymentIamportResponse;
            } else {
                throw new OrderException("결제 검증 실패 : ");
            }
        } catch (Exception e) {
            throw new Exception("결제 검증 중 오류 발생: " + e.getMessage());
        }
    }
}