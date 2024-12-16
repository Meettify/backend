package com.example.meettify.controller.naver_search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "네이버 검색", description = "네이버 검색 API 사용 컨트롤러")
public interface SearchServeControllerDocs {
    @Operation(summary = "장소 검색", description = "네이버 검색 API를 이용하여 지정된 이름으로 장소를 검색")
    List<Map<String, String>> naverSearch(String name);
    @Operation(summary = "장소 동적 검색", description = "네이버 검색 API를 이용하여 동적으로 검색어를 지정하여 장소를 검색")
    List<Map<String, String>> naverSearchDynamic(String query);
}
