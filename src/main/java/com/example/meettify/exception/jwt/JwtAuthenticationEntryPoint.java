package com.example.meettify.exception.jwt;

import com.example.meettify.config.slack.RequestInfo;
import com.example.meettify.config.slack.SlackUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
/*
 *   worker : 유요한
 *   work   : 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 발생하는 예외를 처리하는 클래스
 *            주로 JWT 기반 인증을 사용하는 애플리케이션에서, 인증이 실패하거나 토큰이 없을 때, 이를 처리하는 역할
 *   date   : 2024/09/19
 *   update : 2024/10/26
 * */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final SlackUtil slackUtil;
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        slackUtil.sendAlert(authException, new RequestInfo(request)); // Slack 알림 전송
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
