package com.example.meettify.entity.cart;

import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.entity.item.ItemEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/*
 *   writer  : 유요한
 *   work    : 장바구니 상품 정보를 담아줄 엔티티
 *   date    : 2024/10/21
 * */
@Entity(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(exclude = {"cart", "item"})
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @Column(name = "cart_count")
    private int cartCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    // 장바구니 상품 추가
    public static CartItemEntity addCartItem(RequestCartServiceDTO cartItem,
                                             CartEntity cart,
                                             ItemEntity item) {
        return CartItemEntity.builder()
                .cart(cart)
                .cartCount(cartItem.getItemCount())
                .item(item)
                .build();
    }

    // 기존의 상품 수량에 합치기
    public void addCartPlus(RequestCartServiceDTO cartItem) {
        if(Objects.equals(cartItem.getItemId(), this.item.getItemId())) {
            this.cartCount = this.cartCount + cartItem.getItemCount();
        }
    }
}
