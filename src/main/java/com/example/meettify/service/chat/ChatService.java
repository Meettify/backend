package com.example.meettify.service.chat;

import com.example.meettify.dto.chat.ChatMemberDTO;
import com.example.meettify.dto.chat.ChatMessageDTO;
import com.example.meettify.dto.chat.ChatRoomDTO;

import java.util.List;

public interface ChatService {
    // 채팅 메시지 저장 및 전송
    ChatMessageDTO sendMessage(ChatMessageDTO message);
    // 특정 채팅방의 모든 메시지 조회
    List<ChatMessageDTO> getMessagesByRoomId(Long roomId);
    // 채팅방 생성
    ChatRoomDTO createRoom(String roomName, String email, Long meetId);
    // 본인이 속한 채팅방 리스트
    List<ChatRoomDTO> getRooms(String email);
    // 채팅방에 속해있으면 true
    boolean joinRoom(String email, Long roomId);
    // 채팅방에 들어와 있는 유저 리스트 반환
    List<ChatMemberDTO> getRoomMembers(Long roomId);
    // 채팅방 나가기
    String leaveRoom(String email, Long roomId);
    // 채팅방이 생성되었는지 확인
    boolean checkCreateChatRoom(Long meetId);
}
