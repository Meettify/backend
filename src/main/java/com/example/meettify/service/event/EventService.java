package com.example.meettify.service.event;

import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.ResponseEventDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    ResponseEventDTO createEvent(String title, String content);
    ResponseEventDTO getEvent(Long eventId);
    String deleteEvent(Long eventId);
    ResponseEventDTO updateEvent(Long eventId, UpdateEventDTO event);
    Page<ResponseEventDTO> getEvents(Pageable page);
}
