package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "채팅", description = "채팅 API")
public interface ChatControllerDocs {
    @Operation(summary = "채팅", description = "채팅 생성하는 API")
    void sendMessage(ChatMessageDTO message, int roomId);

//    @Operation(summary = "채팅", description = "채팅 생성하는 API")
//    void receive(ChatMessageDTO chatDTO);

}
