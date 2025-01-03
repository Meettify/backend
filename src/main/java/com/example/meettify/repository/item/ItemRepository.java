package com.example.meettify.repository.item;

import com.example.meettify.entity.item.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, CustomItemRepository {
}
