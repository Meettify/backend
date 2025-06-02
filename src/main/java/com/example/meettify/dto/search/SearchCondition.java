package com.example.meettify.dto.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/*
 *   writer  : 조영흔, 유요한
 *   work    : 전체 검색할 때 동적으로 조건을 받기 위한 클래스
 *   date    : 2024/10/13
 * */
@ToString
@Getter
@AllArgsConstructor
@Builder
public class SearchCondition {
    // total로 검색하는 기능
    private String totalKeyword;
}
