package com.example.meettify.repository.chat;

import com.example.meettify.document.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    // 특정 채팅방의 메시지 조회
    List<ChatMessage> findByRoomId(Long roomId);
}
