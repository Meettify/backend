package com.example.meettify.service.event;

import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.ResponseEventDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import com.example.meettify.entity.coupon.EventEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.repository.event.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;

    // 이벤트 글 생성
    @Override
    public ResponseEventDTO createEvent(RequestEventCouponDTO event) {
        try {
            // 이벤트 엔티티 생성
            EventEntity eventEntity = EventEntity.create(event);
            EventEntity saveEvent = eventRepository.save(eventEntity);
            log.info("saveEvent {}", saveEvent);
            return ResponseEventDTO.change(saveEvent);
        } catch (Exception e) {
            throw new BoardException("이벤트 게시글 생성이 실패했습니다.");
        }
    }

    // 이벤트 게시글 상세페이지
    @Override
    public ResponseEventDTO getEvent(Long eventId) {
        try {
            EventEntity findEvent = eventRepository.findById(eventId)
                    .orElseThrow(() -> new BoardException("이벤트 게시글이 없습니다."));
            log.info("이벤트 게시글 조회 {}", findEvent);
            return ResponseEventDTO.change(findEvent);
        } catch (Exception e) {
            throw new BoardException("이벤트 게시글 조회하는데 실패했습니다.");
        }
    }

    // 이벤트 게시글 삭제
    @Override
    public String deleteEvent(Long eventId) {
        try {
            EventEntity findEvent = eventRepository.findById(eventId)
                    .orElseThrow(() -> new BoardException("이벤트 게시글이 없습니다."));
            log.info("이벤트 게시글 조회 {}", findEvent);
            eventRepository.delete(findEvent);
            return "삭제하는데 성공했습니다.";
        }catch (Exception e) {
            throw new BoardException("이벤트 게시글 삭제하는데 실패했습니다.");
        }
    }

    // 이벤트 게시글 수정
    @Override
    public ResponseEventDTO updateEvent(Long eventId, UpdateEventDTO event) {
        try {
            EventEntity findEvent = eventRepository.findById(eventId)
                    .orElseThrow(() -> new BoardException("이벤트 게시글이 없습니다."));
            log.info("이벤트 게시글 조회 {}", findEvent);

            // 수정
            findEvent.update(event);
            return ResponseEventDTO.change(findEvent);
        }catch (Exception e) {
            throw new BoardException("이벤트 게시글 수정하는데 실패했습니다.");
        }
    }
}
