package com.example.meettify.repository.jpa.item;

import com.example.meettify.entity.item.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, CustomItemRepository {
}
