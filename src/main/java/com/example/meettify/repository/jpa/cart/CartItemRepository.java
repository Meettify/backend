package com.example.meettify.repository.jpa.cart;

import com.example.meettify.entity.cart.CartItemEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    CartItemEntity findByCartCartId(Long cartId);

    @Query("select c from cart_item c" +
            " join fetch c.item" +
            " where c.cart.cartId = :cartId")
    List<CartItemEntity> findAllByCartCartId(@Param("cartId") Long cartId);

    @Query("select c from cart_item c" +
            " join fetch c.item" +
            " join fetch c.cart" +
            " where c.item.itemId = :itemId")
    CartItemEntity findByItem_ItemId(Long itemId);

    void deleteByItem_ItemId(Long itemId);

    Slice<CartItemEntity> findByCart_MemberMemberEmail(String memberEmail, Pageable page);

}
