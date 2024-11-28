package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController implements ChatControllerDocs {
    private final ChatService chatService;

    @Override
    // 클라이언트에서 서버로 보낸 메시지를 메시지를 라우팅
    @MessageMapping("/{roomId}")
    // 구독한 클라이언트에게 response를 제공할 url 정의
    @SendTo("/pub/{roomId}")
    // @DestinationVariable: 구독 및 메시징의 동적 url 변수를 설정. RestAPI의 @PathValue와 같다.
    // @Payload: 메시지의 body를 정의한 객체에 매핑합니다.
    public ResponseEntity<?> sendMessage(@Payload ChatMessageDTO message,
                                         @DestinationVariable Long roomId) {
        try {
            chatService.sendMessage(message);
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            throw new ChatException(e.getMessage());
        }
    }


}
