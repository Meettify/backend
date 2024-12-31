package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.ChatMemberDTO;
import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.ChatRoomDTO;
import com.example.meettify.dto.chat.RequestAccessEmailDTO;
import com.example.meettify.exception.chat.ChatRoomException;
import com.example.meettify.exception.member.MemberException;
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
                                            @RequestParam Long meetId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ChatRoomDTO response = chatService.createRoom(roomName, email, meetId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ChatRoomException(e.getMessage());
        }
    }

    // 채팅방 리스트
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

    // 채팅방에 속한 채팅 내역 가져오기
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

    // 채팅방에 들어갈 때 체크
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

    // 채팅방 유저 리스트 조회
    @Override
    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getRoomMembers(@PathVariable Long roomId) {
        try {
            List<ChatMemberDTO> response = chatService.getRoomMembers(roomId);
            log.info("response: {}", response);
            return ResponseEntity.ok(response);
        } catch (ChatRoomException e) {
            throw new ChatRoomException(e.getMessage());
        } catch (MemberException e) {
            throw new MemberException(e.getMessage());
        }
    }

    // 채팅방 나가기
    @Override
    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> leaveRoom(@PathVariable Long roomId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String response = chatService.leaveRoom(email, roomId);
            log.info("response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ChatRoomException(e.getMessage());
        }
    }

    // 모임글 번호로 채팅방 조회
    @Override
    @GetMapping("/{meetId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> checkChatRoom(@PathVariable Long meetId) {
        try {
            boolean response = chatService.checkCreateChatRoom(meetId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ChatRoomException(e.getMessage());
        }
    }
}
