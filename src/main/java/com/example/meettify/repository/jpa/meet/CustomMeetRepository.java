package com.example.meettify.repository.jpa.meet;

import com.example.meettify.dto.meet.MeetSearchCondition;
import com.example.meettify.entity.meet.MeetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 *   worker  : 조영흔
 *   work    : QueryDsl을 사용하기 위한 커스텀 레포지토리
 *   date    : 2024/10/11
 * */
public interface CustomMeetRepository {
    Page<MeetEntity> meetsSearch(MeetSearchCondition condition, Pageable pageable);

}
