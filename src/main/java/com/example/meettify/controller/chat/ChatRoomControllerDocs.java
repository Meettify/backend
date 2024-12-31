package com.example.meettify.controller.chat;

import com.example.meettify.dto.chat.RequestAccessEmailDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "채팅방", description = "채팅방 API")
public interface ChatRoomControllerDocs {
    @Operation(summary = "채팅방 생성", description = "채팅방 생성하는 API")
    ResponseEntity<?> createChatRoom(String roomName,
                                     Long meetId,
                                     UserDetails userDetails);
    @Operation(summary = "채팅방 리스트", description = "채팅방 리스트 API")
    ResponseEntity<?> getRooms(UserDetails userDetails);
    @Operation(summary = "채팅방 입장시 체크", description = "채팅방 입장시 임시번호를 체크하고 해당방에 속해있는지 확인하는 API")
    ResponseEntity<?> joinRoom(RequestAccessEmailDTO requestAccessRoomId, Long roomId);
    @Operation(summary = "채팅 내역", description = "채팅 내역 API")
    ResponseEntity<?> getMessagesByRoomId(Long roomId);
    @Operation(summary = "채팅방 유저 리스트", description = "채팅방 유저 리스트 API")
    ResponseEntity<?> getRoomMembers(Long roomId);
    @Operation(summary = "채팅방 나가기", description = "채팅방 나가기 API")
    ResponseEntity<?> leaveRoom(Long roomId, UserDetails userDetails);
    @Operation(summary = "모임글 번호로 채팅방 조회", description = "모임글 번호로 채팅방 조회 API")
    ResponseEntity<?> checkChatRoom(Long meetId);
}
