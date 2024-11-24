package com.example.meettify.controller.pay;

import com.example.meettify.config.iamport.ImportConfig;
import com.example.meettify.config.pay.PaymentClient;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.dto.pay.*;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.exception.pay.PayException;
import com.example.meettify.exception.pay.PaymentCancelException;
import com.example.meettify.exception.pay.PaymentConfirmException;
import com.example.meettify.service.notification.NotificationService;
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
    private final PaymentClient paymentClient;
    private final NotificationService notificationService;



    // 아임포트 결제 검증
    @PostMapping("/verify")
    @PreAuthorize("hasRole('ROLE_USER')")
    public IamportResponse<Payment> payForOrder(@RequestBody RequestPaymentDTO pay,
                                                @RequestBody AddressDTO address,
                                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            log.info("결제 검증 서비스 실행");
            String email = userDetails != null ? userDetails.getUsername() : null;
            IamportResponse<Payment> paymentIamportResponse = importConfig.iamportClient().paymentByImpUid(pay.getImpUid());

            if (paymentIamportResponse.getResponse().getStatus().equals("paid")) {
                // 결제 성공 시 주문 정보 저장
                ResponseOrderDTO response = orderService.saveOrder(pay.getOrders(), email, address, pay.getOrderUid());
                // 결제 정보를 저장
                ResponsePaymentDTO responsePaymentDTO = paymentService.savePayment(pay, email, address, paymentIamportResponse);
                log.info(response);
                log.info(responsePaymentDTO);
                // 결제 알림
                notificationService.notifyMessage(email, "결제하셨습니다.");

                return paymentIamportResponse;
            } else {
                throw new OrderException("결제 검증 실패 : ");
            }
        } catch (Exception e) {
            throw new Exception("결제 검증 중 오류 발생: " + e.getMessage());
        }
    }

    // 아임포트 결제 취소
    @Override
    @PostMapping("/iamport/cancel")
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

    // 토스 결제 검증
    @Override
    @PostMapping("/confirm")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseTossPaymentConfirmDTO confirmTossPayment(@RequestBody RequestTossPaymentConfirmDTO tossPay,
                                                            @RequestBody AddressDTO address,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails != null ? userDetails.getUsername() : null;
            ResponseTossPaymentConfirmDTO response = paymentClient.confirmPayment(tossPay);
            log.info(response);
            // 결제 성공 시 주문 정보 저장
            orderService.saveOrder(tossPay.getOrders(), email, address, tossPay.getOrderUid());
            // 결제 알림
            notificationService.notifyMessage(email, "토스로 결제 :" + response.getTotalAmount() +"원이 결제되었습니다.");
            return response;
        } catch (PaymentConfirmException e) {
            throw new PaymentConfirmException(e.getMessage());
        }
    }

    // 토스 결제 취소
    @PostMapping("/toss/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseTossCancelDTO cancelTossPayment(@RequestBody TossPaymentCancelDTO payment) {
        try {
            // 결제 취소 요청
            ResponseTossCancelDTO cancelResponse = paymentClient.cancelPayment(payment);

            // 결제 취소가 성공적으로 이루어진 경우에만 주문 취소
            if ("CANCELED".equals(cancelResponse.getStatus())) { // 취소 상태 확인
                orderService.cancelOrder(payment.getOrderId());
            }

            return cancelResponse;
        } catch (PaymentCancelException e) {
            throw new PaymentCancelException(e.getMessage());
        }
    }
}