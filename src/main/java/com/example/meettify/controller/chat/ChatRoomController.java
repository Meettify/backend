package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.ChatRoomDTO;
import com.example.meettify.dto.chat.RequestAccessEmailDTO;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/chat")
public class ChatRoomController implements ChatRoomControllerDocs {
    private final ChatService chatService;

    // 채팅방 생성
    @Override
    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createChatRoom(@RequestParam String roomName,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ChatRoomDTO response = chatService.createRoom(roomName, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ChatRoomException(e.getMessage());
        }
    }

    @Override
    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getRooms(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            List<ChatRoomDTO> response = chatService.getRooms(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ChatRoomException(e.getMessage());
        }
    }


    @Override
    @GetMapping("/{roomId}/messages")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMessagesByRoomId(@PathVariable Long roomId) {
        try {
            List<ChatMessageDTO> response = chatService.getMessagesByRoomId(roomId);
            return ResponseEntity.ok(response);
        } catch (ChatRoomException e) {
            log.error(e.getMessage());
            throw new ChatRoomException(e.getMessage());
        }
    }

    @Override
    @PostMapping("/{roomId}/access")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> joinRoom(@RequestBody RequestAccessEmailDTO accessEmail,
                                      @PathVariable Long roomId) {
        try {
            boolean response = chatService.joinRoom(accessEmail.getAccessEmail(), roomId);
            return ResponseEntity.ok(response);
        } catch (ChatRoomException e) {
            throw new ChatRoomException(e.getMessage());
        }
    }
}
