package com.example.meettify.repository.notification;

import com.example.meettify.entity.notification.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByMemberMemberEmailOrderByRegTimeDesc(String email);
}
