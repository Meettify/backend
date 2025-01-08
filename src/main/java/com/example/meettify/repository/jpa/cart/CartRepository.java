package com.example.meettify.repository.jpa.cart;

import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.cart.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByMemberMemberId(Long memberId);

    @Query("select c from carts c" +
            " join fetch c.member" +
            " join fetch c.cartItems ci" + // CartItemEntity를 가져옴
            " left join fetch ci.item " + // ItemEntity를 가져옴
            " where c.cartId = :cartId")
    CartEntity findByCartId(@Param("cartId") Long cartId);

    CartEntity findByMemberMemberEmail(String memberEmail);
}
