package com.example.meettify.repository.jpa.order;

import com.example.meettify.dto.order.PayStatus;
import com.example.meettify.entity.order.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomOrderRepository {
    Page<OrderEntity> findAllOrders(Pageable page, PayStatus payStatus);
}
