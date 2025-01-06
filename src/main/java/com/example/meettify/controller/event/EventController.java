package com.example.meettify.controller.event;

import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.ResponseEventDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController implements EventControllerDocs{
    private final EventService eventService;

    // 이벤트 생성
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createEvent(RequestEventCouponDTO event) {
        try {
            ResponseEventDTO response = eventService.createEvent(event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 이벤트 상세 페이지
    @Override
    @GetMapping("/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) {
        try {
            ResponseEventDTO response = eventService.getEvent(eventId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 이벤트 삭제
    @Override
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        try {
            String response = eventService.deleteEvent(eventId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 이벤트 수정
    @Override
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId,
                                         @RequestBody UpdateEventDTO event) {
        try {
            ResponseEventDTO response = eventService.updateEvent(eventId, event);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }
}
