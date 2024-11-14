package com.example.meettify.config.pay;

import com.example.meettify.dto.pay.*;
import com.example.meettify.exception.pay.PaymentConfirmErrorCode;
import com.example.meettify.exception.pay.PaymentConfirmException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class PaymentClient {
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";
    private static final int CONNECT_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 30;
    private final ObjectMapper objectMapper;
    private final PaymentProperties paymentProperties;
    private final RestClient restClient;

    public PaymentClient(PaymentProperties paymentProperties,
                         ObjectMapper objectMapper) {
        this.paymentProperties = paymentProperties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .requestFactory(createPaymentRequestFactory())
                .requestInterceptor(new PaymentExceptionInterceptor())
                .requestInterceptor(new PaymentLoggingInterceptor()) // 로깅 인터셉터 등록
                .defaultHeader(HttpHeaders.AUTHORIZATION, createPaymentAuthHeader(paymentProperties))
                .build();
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
    public ResponseTossPaymentConfirmDTO confirmPayment(RequestTossPaymentConfirmDTO tossPay) {
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getConfirmUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossPay)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new PaymentConfirmException(getPaymentConfirmErrorCode(response));
                }))
                .body(ResponseTossPaymentConfirmDTO.class);
    }

    // 결제 승인 API 에러 코드 문서
    private PaymentConfirmErrorCode getPaymentConfirmErrorCode(final ClientHttpResponse response) throws IOException {
        PaymentFailOutput confirmFailResponse = objectMapper.readValue(
                response.getBody(), PaymentFailOutput.class);
        return PaymentConfirmErrorCode.findByName(confirmFailResponse.code());
    }

    // 결제 취소 API 에러 코드 문서
    public ResponseTossCancelDTO cancelPayment(TossPaymentCancelDTO payment) {
        TossCancelDTO cancel = new TossCancelDTO(payment.getAmount(), payment.getCancelReason());
        return restClient.method(HttpMethod.POST)
                .uri(paymentProperties.getCancelUrl(payment.getPaymentKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancel)
                .retrieve()
                .body(ResponseTossCancelDTO.class);
    }
}
