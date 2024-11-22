package com.example.meettify.repository.notification;

import com.example.meettify.entity.notification.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
