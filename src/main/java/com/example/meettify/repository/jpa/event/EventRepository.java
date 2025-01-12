package com.example.meettify.repository.jpa.event;

import com.example.meettify.entity.coupon.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Page<EventEntity> findAllByOrderByEventIdDesc(Pageable page);
}
