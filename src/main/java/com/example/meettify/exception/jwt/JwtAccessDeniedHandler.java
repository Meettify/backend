package com.example.meettify.exception.jwt;

import com.example.meettify.config.slack.RequestInfo;
import com.example.meettify.config.slack.SlackUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/*
 *   worker : 유요한
 *   work   : JWT가 접근 권한이 없을 때 호출되는 역할입니다.
 *   date   : 2024/09/19
 *   update : 2024/10/26
 * */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final SlackUtil slackUtil;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        slackUtil.sendAlert(accessDeniedException, new RequestInfo(request)); // Slack 알림 전송
        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    }
}
