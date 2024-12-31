package com.example.meettify.config.pay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment")
@Setter
@Getter
public class PaymentProperties {
    @Value("${payment.secret_key}")
    private String secretKey;
    @Value("${payment.base-url}")
    private String baseUrl;
    @Value("${payment.confirm-endpoint}")
    private String confirmEndpoint;
    @Value("${payment.cancel-endpoint}")
    private String cancelEndpoint;

    public String getConfirmUrl() {
        return baseUrl + confirmEndpoint;
    }

    public String getCancelUrl(String paymentKey) { // cancelUrl 흭득 메서드 추가
        return String.format(baseUrl + cancelEndpoint, paymentKey);
    }
}
