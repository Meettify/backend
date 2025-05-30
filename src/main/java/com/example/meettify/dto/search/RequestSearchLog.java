package com.example.meettify.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 *   writer  : 유요한
 *   work    : 최근 검색 등록을 위한 DTO
 *   date    : 2024/10/24
 * */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RequestSearchLog {
    private String name;
}
