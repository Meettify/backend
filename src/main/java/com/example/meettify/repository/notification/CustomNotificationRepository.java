package com.example.meettify.repository.notification;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CustomNotificationRepository {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // memberId를 사용하여 해당 클라이언트의 SseEmitter 객체를 조회한다.
    // boardcast를 위해 구독 중인 사용자의 SseEmitter를 조회한다.
    public SseEmitter findById(long memberId) {
        return emitters.get(memberId);
    }

    // 해당 클라이언트를 위한 SseEmitter를 생성하고 Map에 저장한다.
    public SseEmitter save(Long memberId, SseEmitter sseEmitter) {
        emitters.put(memberId, sseEmitter);
        return emitters.get(memberId);
    }

    public void deleteById(Long memberId) {
        emitters.remove(memberId);
    }

    public boolean containsKey(Long memberId) {
        return emitters.containsKey(memberId);
    }
}
