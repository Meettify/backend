package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController implements ChatControllerDocs {
    private final ChatService chatService;

    @Override
    /*STOMP*/
    @MessageMapping("/{roomId}")
    // 구독한 클라이언트에게 response를 제공할 url 정의
    @SendTo("/topic/{roomId}")
    public ResponseEntity<?> sendMessage(
            // @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
            @Payload ChatMessageDTO message,
            // @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
            @DestinationVariable int roomId) {
        try {
            ChatMessageDTO msg = chatService.sendMessage(message);
            log.info("Sent message: {}", msg);

            return ResponseEntity.ok().body(msg);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            throw new ChatException(e.getMessage());
        }
    }
}
