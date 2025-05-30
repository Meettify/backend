package com.example.meettify.config.websocket;

import com.example.meettify.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    // WebSocket 연결 시 헤더에서 JWT token 유효성 검증
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        /**
         * 1. StompHeaderAccessor: Stomp 메세지의 헤더에 접근하는 클래스
         * 2. 전송된 Stomp 메세지의 Command가 CONNECT인지 검사
         * 3. StompHeaderAccessor로부터 Authorization 헤더의 JWT 토큰 추출(BEARER <- 이거 제거)
         * 4. jwtAuthenticationFilter로부터 유효한 토큰인지 확인
         */
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            log.debug("Authorization header: {}", authorization);

            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                log.debug("토큰 체크 {}", token);
                if (!jwtProvider.validateToken(token)) {
                    throw new AccessDeniedException("유효하지 않은 토큰입니다.");
                }
                Authentication authentication = jwtProvider.getAuthentication(token);
                log.debug("인증에 성공했습니다.");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new AccessDeniedException("토큰이 존재하지 않습니다.");
            }
        }

        return message; // 반드시 return 해줘야 메시지가 흐름을 탑니다.
    }
}
