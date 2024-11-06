package com.example.meettify.repository.item;

import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.item.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/*
*   worker  : 유요한
*   work    : QueryDsl을 사용하기 위한 커스텀 레포지토리
*   date    : 2024/10/09
* */
public interface CustomItemRepository {
    Page<ItemEntity> itemsSearch(ItemSearchCondition condition, Pageable pageable);
    long countItems(ItemSearchCondition condition);

    List<ItemEntity> findItemsByCategoriesAndKeyword(Set<Category> categories, String keyword);
}
