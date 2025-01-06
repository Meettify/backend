package com.example.meettify.controller.event;

import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "이벤트", description = "이벤트 컨트롤러")
public interface EventControllerDocs {
    @Operation(summary = "이벤트 생성", description = "이벤트 생성 api")
    ResponseEntity<?> createEvent(RequestEventCouponDTO event);
    @Operation(summary = "이벤트 상세페이지", description = "이벤트 상세페이지 api")
    ResponseEntity<?> getEvent(Long eventId);
    @Operation(summary = "이벤트 삭제", description = "이벤트 삭제 api")
    ResponseEntity<?> deleteEvent(Long eventId);
    @Operation(summary = "이벤트 수정", description = "이벤트 수정 api")
    ResponseEntity<?> updateEvent(Long eventId, UpdateEventDTO event);
}
