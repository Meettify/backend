package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.MessageType;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController implements ChatControllerDocs {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    /*STOMP*/
    @MessageMapping("/{roomId}")
    public void sendMessage(
            // @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
            @Payload ChatMessageDTO message,
            // @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
            @DestinationVariable Long roomId) {
        try {
            ChatMessageDTO msg = chatService.sendMessage(message, roomId);
            log.info("Sent message: {}", msg);

            // 구독자에게 직접 전송
            messagingTemplate.convertAndSend("/topic/" + roomId, msg);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            throw new ChatException(e.getMessage());
        }
    }
}
