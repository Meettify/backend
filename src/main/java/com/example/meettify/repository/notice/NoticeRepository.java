package com.example.meettify.repository.notice;

import com.example.meettify.entity.notice.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
}
