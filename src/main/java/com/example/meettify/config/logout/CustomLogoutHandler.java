package com.example.meettify.config.logout;

import com.example.meettify.service.jwt.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenBlackListService tokenBlackListService;

    /**
     * 로그아웃 엔드포인트로 호출되면 이에 대해 처리합니다.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the current principal details
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            log.debug("로그아웃이 수행됩니다.");

            // 1. 요청한 값에서 토큰을 추출
            String headerToken = request.getHeader("Authorization");

            // 2. Authorization 헤더가 없는 경우 예외 발생
            if (headerToken == null) {
                throw new IllegalArgumentException("Authorization 헤더가 존재하지 않습니다.");
            }

            // 3. 실제 토큰 값을 확인
            if (StringUtils.hasText(headerToken) && headerToken.startsWith("Bearer ")) {
                String token = headerToken.substring(7);

                // 4. Redis 내에 토큰이 존재하지 않는 경우
                if (!tokenBlackListService.containToken(token)) {
                    // 5. BlackList에 추가
                    tokenBlackListService.addTokenToList(token);
                    Set<Object> blackList = tokenBlackListService.getTokenBlackList();
                    log.debug("blackList {}", blackList);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            log.error("로그아웃 중 예외 발생", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
