package com.example.meettify.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@RequiredArgsConstructor
@Configuration
// 웹소켓 활성화 : 스프링에서 제공하는 내장 메시지 브로커(SimpleBroker)를 사용
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    private final StompExceptionHandler stompExceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .setErrorHandler(stompExceptionHandler)
                // 소켓 연결 URI다. 소켓을 연결할 때 다음과 같은 통신이 이루어짐
                .addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:*")
                // SocketJS를 통해 연결 지원
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.debug("동작함");
        registration.interceptors(stompHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/send");
    }

    // 클라이언트가 WebSocket을 통해 서버와 연결했을 때 발생하는 이벤트를 처리
    @EventListener
    public void connectEvent(SessionConnectedEvent event) {
        log.debug("session connected {}", event);
        log.debug("연결 성공!!!!!!!!!!!!!!!!!!");
    }

    // 클라이언트가 WebSocket을 통해 서버와 연결했을 때 발생하는 이벤트를 처리
    @EventListener
    public void disconnectEvent(SessionDisconnectEvent event) {
        log.debug("session disconnected {}", event);
        log.debug("연결 끊어짐!!!!!!!!!!!!!!!!!");
    }
}
