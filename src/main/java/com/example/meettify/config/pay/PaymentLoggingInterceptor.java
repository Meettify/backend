package com.example.meettify.config.pay;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Log4j2
public class PaymentLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String host = request.getURI().getHost();
        String path = request.getURI().getPath();
        String httpMethod = request.getMethod().toString();

        log.info("[Payment Request] {}\t: {} {} \n \t{}", path, httpMethod, host, new String(body));
        return execution.execute(request, body);
    }
}
