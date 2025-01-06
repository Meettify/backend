package com.example.meettify.repository.event;

import com.example.meettify.entity.coupon.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
