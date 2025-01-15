package com.example.meettify.controller.event;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;
import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.ResponseEventDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.coupon.CouponService;
import com.example.meettify.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController implements EventControllerDocs{
    private final EventService eventService;
    private final CouponService couponService;

    // 이벤트 생성
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody RequestEventCouponDTO event) {
        try {

            // 이벤트 제목
            String title = event.getTitle();
            // 이벤트 내용
            String content = event.getContent();
            // 개수
            int count = event.getCouponCount();
            // 쿠폰 정보
            RequestCouponDTO coupon = event.getCoupon();

            // 이벤트 생성
            ResponseEventDTO responseEvent = eventService.createEvent(title, content, count);
            // 쿠폰 생성
            ResponseCouponDTO responseCoupon = couponService.createCoupon(coupon, responseEvent.getEventId());
            // 이벤트에 쿠폰 추가
            responseEvent.addCoupon(responseCoupon);
            return ResponseEntity.ok(responseEvent);
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

    // 이벤트 페이징
    @Override
    @GetMapping("")
    public ResponseEntity<?> getEvents(@PageableDefault(sort = "eventId", direction = Sort.Direction.DESC) Pageable page) {
        try {
            Page<ResponseEventDTO> events = eventService.getEvents(page);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("events", events.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", events.getNumber());
            // 전체 페이지 수
            response.put("totalPage", events.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", events.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", events.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", events.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", events.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", events.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }
}
