package com.example.meettify.config.pay;

import com.example.meettify.dto.pay.*;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.pay.PaymentConfirmErrorCode;
import com.example.meettify.exception.pay.PaymentConfirmException;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.order.OrderRepository;
import com.example.meettify.repository.jpa.pay.TossPaymentRepository;
import com.example.meettify.service.order.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Slf4j
@Component
public class PaymentClient {
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";
    private static final int CONNECT_TIMEOUT_SECONDS = 3;
    private static final int READ_TIMEOUT_SECONDS = 30;
    private final ObjectMapper objectMapper;
    private final PaymentProperties paymentProperties;
    private final RestClient restClient;
    private final MemberRepository memberRepository;
    @Value("${payment.secret_key}")
    private String secretKey;

    public PaymentClient(PaymentProperties paymentProperties,
                         ObjectMapper objectMapper, MemberRepository memberRepository, TossPaymentRepository tossPaymentRepository, OrderService orderService, OrderRepository orderRepository) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymentRequestFactory())
                .requestInterceptor(new PaymentExceptionInterceptor())
                .requestInterceptor(new PaymentLoggingInterceptor()) // 로깅 인터셉터 등록
                .defaultHeader(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader(paymentProperties))
                .build();
        this.memberRepository = memberRepository;
    }

    private ClientHttpRequestFactory createPaymentRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory.class, settings);
    }

    private String createPaymentAuthHeader(PaymentProperties paymentProperties) {
        byte[] encodedBytes = Base64.getEncoder().encode((paymentProperties.getSecretKey() + BASIC_DELIMITER).getBytes(StandardCharsets.UTF_8));
        return AUTH_HEADER_PREFIX + new String(encodedBytes);
    }

    // 결제 요청 API 호출
    public ResponseTossPaymentConfirmDTO confirmPayment(RequestTossPaymentConfirmDTO tossPay, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            log.debug("Payment Secret Key: " + secretKey);

            // 내부 클래스 객체 생성
            TossConfirmRequest request = TossConfirmRequest.builder()
                    .paymentKey(tossPay.getPaymentKey())
                    .orderId(tossPay.getOrderUid())
                    .amount(tossPay.getAmount())
                    .build();

            log.debug("토스 요청 데이터 확인 {}", request);

            // HTTP 요청 후 응답을 ResponseEntity로 수동 처리
            ResponseEntity<String> responseEntity = restClient.method(HttpMethod.POST)
                    .uri(paymentProperties.getConfirmUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader())  // 수정된 헤더 설정
                    .body(request)
                    .retrieve()
                    .toEntity(String.class);

            log.debug("토스 컨펌 데이터 확인 : " + responseEntity);

            // 응답 상태 코드 및 본문 검증
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                // JSON 파싱 및 객체 매핑
                ResponseTossPaymentConfirmDTO tossPayDTO = parseResponse(responseEntity.getBody());
                log.debug("결제 확인 성공: {}", tossPayDTO);

                return tossPayDTO;
            } else {
                throw new PaymentConfirmException("결제 확인 API 호출 실패: 상태코드 - " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new PaymentConfirmException(e.getMessage());
        }
    }

    // Toss 서버에 보낼 최소 정보만 포함한 내부 static class
    @Getter
    @ToString
    @AllArgsConstructor
    @Builder
    private static class TossConfirmRequest {
        private final String paymentKey;
        private final String orderId;
        private final Long amount;
    }


    private String createPaymentAuthHeader() {
        String authValue = paymentProperties.getSecretKey() + ":";
        byte[] encodedBytes = Base64.getEncoder().encode(authValue.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    // JSON 문자열을 ResponseTossPaymentConfirmDTO로 변환
    private ResponseTossPaymentConfirmDTO parseResponse(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, ResponseTossPaymentConfirmDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("결제 응답 매핑 실패: {}", responseBody, e);
            throw new PaymentConfirmException("결제 응답 매핑 실패"+ e);
        }
    }


    // 결제 승인 API 에러 코드 문서
    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), PaymentFailOutput.class);
        return PaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }

    // 결제 취소 API 에러 코드 문서
    public ResponseTossCancelDTO cancelPayment(RequestTossPaymentCancelDTO payment) {
        TossCancelDTO cancel = new TossCancelDTO(payment.getAmount(), payment.getCancelReason());
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(payment.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancel)
                .retrieve()
                .body(ResponseTossCancelDTO.class);
    }
}
