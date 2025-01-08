package com.example.meettify.repository.jpa.notification;

import com.example.meettify.entity.notification.NotificationEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomNotificationRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();
    private final EntityManager entityManager;


    // 해당 클라이언트를 위한 SseEmitter를 생성하고 Map에 저장한다.
    public SseEmitter save(String eventId, SseEmitter sseEmitter) {
        emitters.put(eventId, sseEmitter);
        return emitters.get(eventId);
    }

    // 전송되지 못한 데이터를 캐시로 보관하기 위함
    // 저장된 이벤트는 사용자가 구독할 때, 클라이언트로 전송되어 이벤트의 유실을 방지!
    public void  saveEventCacheId(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    // 캐시에 존재하는지 체크
    public boolean existsEventCache(String eventId) {
        return eventCache.containsKey(eventId);
    }

    // memberId를 사용하여 해당 클라이언트의 SseEmitter 객체를 조회한다.
    // boardcast를 위해 구독 중인 사용자의 SseEmitter를 조회한다.
    public SseEmitter findById(String eventId) {
        return emitters.get(eventId);
    }

    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(Long memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(String.valueOf(memberId)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByMemberId(Long memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(String.valueOf(memberId)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public void deleteById(String eventId) {
        emitters.remove(eventId);
    }


    public void deleteAllEmitterStartWithId(Long memberId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(String.valueOf(memberId))) {
                        emitters.remove(key);
                    }
                }
        );
    }

    public void deleteAllEventCacheStartWithId(Long memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(String.valueOf(memberId))) {
                        eventCache.remove(key);
                    }
                }
        );
    }

    public List<NotificationEntity> findNotificationsWithLimitAndOffset(String email, int limit, int offset) {
        String query = String.format(
                "SELECT * FROM notification n WHERE n.member_email = '%s' ORDER BY n.reg_time DESC LIMIT %d OFFSET %d",
                email, limit, offset
        );

        return entityManager.createNativeQuery(query, NotificationEntity.class).getResultList();
    }

    public void deleteEventCache(String eventId) {
        eventCache.remove(eventId);
    }
}
