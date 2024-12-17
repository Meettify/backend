package com.example.meettify.config.pay;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TossPaymentConfig {
    @Value("${payment.client_key}")
    private String clientKey;
    @Value("${payment.secret_key}")
    private String secretKey;
    @Value("${payment.success_url}")
    private String successUrl;
    @Value("${payment.fail_url}")
    private String failUrl;

    public static final String URL = "https://api.tosspayments.com/v1/payments/";
}
