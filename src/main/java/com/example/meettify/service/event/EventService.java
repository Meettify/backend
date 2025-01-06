package com.example.meettify.service.event;

import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.ResponseEventDTO;
import com.example.meettify.dto.event.UpdateEventDTO;

public interface EventService {
    ResponseEventDTO createEvent(RequestEventCouponDTO event);
    ResponseEventDTO getEvent(Long eventId);
    String deleteEvent(Long eventId);
    ResponseEventDTO updateEvent(Long eventId, UpdateEventDTO event);
}
