package com.example.meettify.exception.jwt;

import com.example.meettify.config.slack.RequestInfo;
import com.example.meettify.config.slack.SlackUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

        Gson gson = new Gson();

        // 상세한 에러 정보 추가
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", "FORBIDDEN");
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("exception", accessDeniedException.getMessage());

        // JSON 변화
        String json = gson.toJson(errorDetails);

        // 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // 응답 출력
        PrintWriter printWriter = response.getWriter();
        printWriter.println(json);
        printWriter.flush();
    }
}
