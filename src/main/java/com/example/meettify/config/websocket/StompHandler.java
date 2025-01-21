package com.example.meettify.config.websocket;

import com.example.meettify.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    // WebSocket 연결 시 헤더에서 JWT token 유효성 검증
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            /**
             * 1. StompHeaderAccessor: Stomp 메세지의 헤더에 접근하는 클래스
             * 2. 전송된 Stomp 메세지의 Command가 CONNECT인지 검사
             * 3. StompHeaderAccessor로부터 Authorization 헤더의 JWT 토큰 추출(BEARER <- 이거 제거)
             * 4. jwtAuthenticationFilter로부터 유효한 토큰인지 확인
             */
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if(accessor.getCommand() == StompCommand.CONNECT) {
                String authorization = accessor.getFirstNativeHeader("Authorization");
                log.info(authorization);

                log.info("======================================");
                log.info("Received STOMP Message {}", message);
                log.info("All Headers {}", accessor.toNativeHeaderMap());
                log.info("======================================");

                if (authorization != null && authorization.startsWith("Bearer ")) {
                    String token = authorization.substring(7);
                    if(!jwtProvider.validateToken(token)) {
                        throw new AccessDeniedException("Access denied");
                    }
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    log.info("인증에 성공했습니다. ");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            throw new AccessDeniedException("Authorization not found");
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
}
