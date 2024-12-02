package com.example.meettify.service.chat;

import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.ChatRoomDTO;

import java.util.List;

public interface ChatService {
    // 채팅 메시지 저장 및 전송
    void sendMessage(ChatMessageDTO message);
    // 특정 채팅방의 모든 메시지 조회
    List<ChatMessageDTO> getMessagesByRoomId(Long roomId);
    // 채팅방 생성
    ChatRoomDTO createRoom(String roomName, String email);
    // 본인이 속한 채팅방 리스트
    List<ChatRoomDTO> getRooms(String email);
    // 채팅방에 속해있으면 true
    boolean joinRoom(String email, Long roomId);
}