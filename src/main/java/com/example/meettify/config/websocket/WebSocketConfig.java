package com.example.meettify.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Log4j2
@RequiredArgsConstructor
@Configuration
// 웹소켓 활성화 : 스프링에서 제공하는 내장 메시지 브로커(SimpleBroker)를 사용
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    private final StompExceptionHandler stompExceptionHandler;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.username}")
    private String userName;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .setErrorHandler(stompExceptionHandler)
                // 소켓 연결 URI다. 소켓을 연결할 때 다음과 같은 통신이 이루어짐
                .addEndpoint("/api/ws/chat")
                .setAllowedOriginPatterns("http://localhost:5173")
                // SocketJS를 통해 연결 지원
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.info("--------------");
        log.info("동작함");
        registration.interceptors(stompHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        registry.setPathMatcher(new AntPathMatcher("."));
        registry.setUserDestinationPrefix("/sub");           // 클라이언트 구독 경로

        // RabbitMQ 브로커 리레이 설정
        registry.enableStompBrokerRelay("/exchange", "/queue")
                .setRelayHost(host)
                .setRelayPort(61613)
                .setClientLogin(userName)
                .setSystemPasscode(password)
                .setSystemLogin(userName)
                .setSystemPasscode(password);
    }

    // 클라이언트가 WebSocket을 통해 서버와 연결했을 때 발생하는 이벤트를 처리
    @EventListener
    public void connectEvent(SessionConnectedEvent event) {
        log.info("session connected {}", event);
        log.info("연결 성공!!!!!!!!!!!!!!!!!!");
    }

    // 클라이언트가 WebSocket을 통해 서버와 연결했을 때 발생하는 이벤트를 처리
    @EventListener
    public void disconnectEvent(SessionDisconnectEvent event) {
        log.info("session disconnected {}", event);
        log.info("연결 끊어짐!!!!!!!!!!!!!!!!!");
    }
}
