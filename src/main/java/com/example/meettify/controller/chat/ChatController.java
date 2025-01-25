package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController implements ChatControllerDocs {
    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    // 클라이언트에서 서버로 보낸 메시지를 메시지를 라우팅
    // @MessageMapping("chat.message")로 설정하여 클라이언트로부터 /pub/chat.message 목적지로 전송된 STOMP 메시지를 처리한다.
    /*RabbitMQ*/
    @MessageMapping("chat.message.{roomId}")
    /*STOMP*/
//    @MessageMapping("/{roomId}")
    // 구독한 클라이언트에게 response를 제공할 url 정의
//    @SendTo("/topic/{roomId}")
    public ResponseEntity<?> sendMessage(
            // @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
            @Payload ChatMessageDTO message,
            // @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
            @DestinationVariable int roomId) {
        try {
            ChatMessageDTO msg = chatService.sendMessage(message);
            log.info("Sent message: {}", msg);

            if (msg != null) {
                // RabbitMQ으로 메시지 전송
                // template.convertAndSend() 메소드를 사용하여 메시지를 RabbitMQ로 전송한다.
                // 메시지는 chat.exchange로 전송되며, 라우팅 키는 room. + 메시지의 방 ID로 구성된다.
                rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + roomId, message);
            } else {
                log.error("Failed to create chat message. User might not be in the chat room. User: {}, Room: {}",
                        message.getSender(), message.getRoomId());
            }

            return ResponseEntity.ok().body(msg);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            throw new ChatException(e.getMessage());
        }
    }

    // 기본적으로 chat.queue가 exchange에 바인딩 되어있기 때문에 모든 메시지 처리
    // receiver()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그 용도)
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(ChatMessageDTO chatDTO) {
        log.info("received {} ", chatDTO.getMessage());
    }
}
