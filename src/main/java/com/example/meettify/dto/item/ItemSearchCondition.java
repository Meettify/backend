package com.example.meettify.dto.item;

import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


/*
 *   writer  : 유요한
 *   work    : 상품 조회할 때 동적으로 조건을 받기 위한 클래스
 *   date    : 2024/10/09
 * */
@ToString
@Getter
@AllArgsConstructor
@Builder
public class ItemSearchCondition {
    @Schema(description = "상품 이름")
    private String title;
    @Schema(description = "상품 카테고리")
    private Category category;
    @Schema(description = "상품 최소 가격")
    @Min(0)
    private  int minPrice;
    @Schema(description = "상품 최대 가격")
    @Min(0)
    private int maxPrice;
    @Schema(description = "상품 상태")
    private ItemStatus status;
}
