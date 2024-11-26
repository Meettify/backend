package com.example.meettify.repository.chat;

import com.example.meettify.entity.chat_room.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    // 자신이 만든 채팅방 조회
    @Query("SELECT c FROM chat_room c WHERE c.createdNickName = :createdNickName")
    List<ChatRoomEntity> findChatRoomsCreatedByNickName(@Param("createdNickName") String createdNickName);

    // 초대받아 들어간 채팅방 조회
    @Query("SELECT c FROM chat_room c WHERE :memberId MEMBER OF c.inviteMemberIds and c.roomId = :roomId")
    ChatRoomEntity findChatRoomsByInviteMember(@Param("memberId") Long memberId, @Param("roomId") Long roomId);

    // 자신이 만든 채팅방과 초대받은 채팅방 모두 조회
    @Query("SELECT c FROM chat_room c WHERE c.createdNickName = :createdNickName OR :memberId MEMBER OF c.inviteMemberIds")
    List<ChatRoomEntity> findChatRoomsByCreatedOrInvited(@Param("createdNickName") String createdNickName,
                                                         @Param("memberId") Long memberId);
}
