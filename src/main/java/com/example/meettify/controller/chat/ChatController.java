package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.ChatRoomDTO;
import com.example.meettify.dto.chat.RequestAccessRoomIdDTO;
import com.example.meettify.dto.chat.ResponseAccessRoomIdDTO;
import com.example.meettify.exception.chat.ChatException;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class ChatController implements ChatControllerDocs {
    private final ChatService chatService;

    @Override
    @MessageMapping("/{roomId}")
    @SendTo("/pub/{roomId}")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO message,
                                         @DestinationVariable Long roomId) {
        try {
            chatService.sendMessage(message);
            return ResponseEntity.ok().body(message);
        } catch (Exception e) {
            throw new ChatException(e.getMessage());
        }
    }


}
