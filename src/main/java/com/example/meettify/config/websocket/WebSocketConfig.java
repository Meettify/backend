package com.example.meettify.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
// 웹소켓 활성화 : 스프링에서 제공하는 내장 메시지 브로커(SimpleBroker)를 사용
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // 소켓 연결 URI다. 소켓을 연결할 때 다음과 같은 통신이 이루어짐
                .addEndpoint("/ws/chat")
                .setAllowedOrigins("http://localhost:5173")
                // SocketJS를 통해 연결 지원
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 받을 때 경로를 설정해주는 함수 - 스프링에서 지원해주는 내장 브로커를 사용하는 함수
        // "/pub" 이라는 prefix가 붙으면 messageBroker가 해당 경로를 가로챈다.
        // 클라이언트는 토픽을 구독할 시 /sub 경로로 요청해야 함
        registry.enableSimpleBroker("/pub");
        // 메시지를 보낼 때 관련 경로를 설정해주는 함수
        // 클라이언트가 메시지를 보낼 때 경로 앞에 해당 경로가 붙어 있으면 Broker로 보낸다.
        registry.setApplicationDestinationPrefixes("/sub");


    }
}
