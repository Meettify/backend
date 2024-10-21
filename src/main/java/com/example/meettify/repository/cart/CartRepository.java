package com.example.meettify.repository.cart;

import com.example.meettify.entity.cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByMemberMemberId(Long memberId);
}
