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
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
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
                }
            }
            throw new AccessDeniedException("Authorization not found");
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
}
