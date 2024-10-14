package com.example.meettify.controller.search;

import com.example.meettify.dto.search.SearchCondition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "search", description = "전체 검색하는 API")

public interface SearchControllerDocs {

    @Operation(summary = "상품 + 모임을 검색하는 API", description = "모임과 상품에 대한 키워드를 받아서 검색을 진행한다.")
    public ResponseEntity<?> getSearch(SearchCondition searchCondition, @AuthenticationPrincipal UserDetails userDetails);
}