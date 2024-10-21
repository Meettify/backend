package com.example.meettify.repository.cart;

import com.example.meettify.entity.cart.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByCartCartId(Long cartId);
}
