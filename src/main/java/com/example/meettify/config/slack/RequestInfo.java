package com.example.meettify.config.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.time.LocalDateTime;

/*
 *   worker : 유요한
 *   work   : 글로벌 예외에서 슬랙으로 메시지 처리할 때 담아줄 클래스입니다.
 *   date   : 2024/10/20
 * */
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RequestInfo {
    private String requestURI;
    private String method;
    private String remoteAddress;

    public RequestInfo(HttpServletRequest request) {
        this.requestURI = request.getRequestURI();
        this.method = request.getMethod();
        this.remoteAddress = request.getRemoteAddr();
    }

    public String requestURL() {
        return requestURI;
    }

    public String method() {
        return method;
    }

    public String remoteAddress() {
        return remoteAddress;
    }
}
