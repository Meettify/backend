package com.example.meettify.dto.meet;


import com.example.meettify.dto.meet.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * writer   :  조영흔
 * work     :  모임 조회할 때 동적으로 조건을 받기 위한 클래스
 * date     : 2024/10/11
 */

@ToString
@Getter
@AllArgsConstructor
@Builder
public class MeetSearchCondition {
    String name;
    private Category category;
}
