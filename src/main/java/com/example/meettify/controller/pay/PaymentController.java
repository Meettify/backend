package com.example.meettify.controller.pay;

import com.example.meettify.config.iamport.ImportConfig;
import com.example.meettify.config.pay.PaymentClient;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/iamport/confirm")
    @PreAuthorize("hasRole('ROLE_USER')")
    public IamportResponse<Payment> payForOrder(@RequestBody RequestPaymentDTO pay,
                                                @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        try {
            log.info("결제 검증 서비스 실행");
            String email = userDetails != null ? userDetails.getUsername() : null;
            IamportResponse<Payment> paymentIamportResponse = importConfig.iamportClient().paymentByImpUid(pay.getImpUid());

            if (paymentIamportResponse.getResponse().getStatus().equals("paid")) {
                // 결제 성공 시 주문 정보 저장
                ResponseOrderDTO response = orderService.saveOrder(pay.getOrders(), email, pay.getAddress(), pay.getOrderUid());
                // 결제 정보를 저장
                ResponsePaymentDTO responsePaymentDTO = paymentService.savePayment(pay, email, paymentIamportResponse);
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
            // 주문취소
            String result = orderService.cancelOrder(cancel.getOrderUid());
            log.info("result: {}", result);
            log.info(response);
            return response;
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }

    // 아임포트 결제 정보 조회
    @Override
    @GetMapping("/iamport/{orderUid}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getImportPayInfo(@PathVariable String orderUid) {
        try {
            ResponsePaymentDTO response = paymentService.getPayment(orderUid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }

    // 토스 결제 검증
    @Override
    @PostMapping("/toss/confirm")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseTossPaymentConfirmDTO>  confirmTossPayment(@RequestBody RequestTossPaymentConfirmDTO tossPay,
                                                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails != null ? userDetails.getUsername() : null;
            ResponseTossPaymentConfirmDTO response = paymentClient.confirmPayment(tossPay, email);
            log.info(response);
            // 결제 성공 시 주문 정보 저장
            orderService.saveOrder(tossPay.getOrders(), email, tossPay.getAddress(), tossPay.getOrderUid());
            // 결제 알림
            notificationService.notifyMessage(email, "토스로 결제 :" + response.getAmount() +"원이 결제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (PaymentConfirmException e) {
            throw new PaymentConfirmException(e.getMessage());
        }
    }

    // 토스 결제 취소
    @PostMapping("/toss/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResponseTossCancelDTO> cancelTossPayment(@RequestBody RequestTossPaymentCancelDTO payment) {
        try {
            // 결제 취소 요청
            ResponseTossCancelDTO cancelResponse = paymentClient.cancelPayment(payment);

            // 결제 취소가 성공적으로 이루어진 경우에만 주문 취소
            if ("CANCELED".equals(cancelResponse.getStatus())) { // 취소 상태 확인
                orderService.cancelOrder(payment.getOrderUid());
            }

            return ResponseEntity.ok(cancelResponse);
        } catch (PaymentCancelException e) {
            throw new PaymentCancelException(e.getMessage());
        }
    }

    @Override
    @GetMapping("/toss/{orderUid}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getTossPayInfo(@PathVariable String orderUid) {
        try {
            ResponseTossPaymentConfirmDTO response = paymentService.getTossPaymentConfirm(orderUid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new PayException(e.getMessage());
        }
    }
}