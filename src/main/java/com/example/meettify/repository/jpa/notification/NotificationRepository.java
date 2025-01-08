package com.example.meettify.repository.jpa.notification;

import com.example.meettify.entity.notification.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    void deleteByMemberMemberId(Long memberId);
}
