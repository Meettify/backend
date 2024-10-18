package com.example.meettify.config.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RequestInfo {
    private String requestURL;
    private String method;
    private String remoteAddress;

    public RequestInfo(HttpServletRequest request) {
        this.requestURL = request.getRequestURL().toString();
        this.method = request.getMethod();
        this.remoteAddress = request.getRemoteAddr();
    }

    public String requestURL() {
        return requestURL;
    }

    public String method() {
        return method;
    }

    public String remoteAddress() {
        return remoteAddress;
    }
}