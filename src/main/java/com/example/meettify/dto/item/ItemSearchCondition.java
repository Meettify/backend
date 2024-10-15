package com.example.meettify.dto.item;

import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
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
    private String title;
    private Category category;
    private  int minPrice;
    private int maxPrice;
    private ItemStatus status;
}
