package com.example.meettify.repository.jpa.item;

import com.example.meettify.entity.item.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, CustomItemRepository {
    @Query("select i from items i " +
            "where i.itemId in :itemIds")
    List<ItemEntity> findByTopItemIds(@Param("itemIds") List<Long> ids);
}
