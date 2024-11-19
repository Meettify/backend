package com.example.meettify.config.pay;

import com.example.meettify.exception.pay.PaymentConfirmException;
import com.example.meettify.exception.pay.PaymentTimeoutException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new PaymentTimeoutException(e.getMessage());
        } catch (Exception e) {
            throw new PaymentConfirmException(e.getMessage());
        }
    }
}
