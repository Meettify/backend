package com.example.meettify.repository.order;

import com.example.meettify.entity.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
