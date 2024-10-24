package com.example.meettify.entity.cart;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/*
 *   writer  : 유요한
 *   work    : 장바구니 정보를 담아줄 엔티티
 *   date    : 2024/10/21
 * */
@Entity(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(of = "cartId")
public class CartEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(name = "cart_total_count")
    private int totalCount;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItemEntity> cartItems = new ArrayList<>();

    // 장바구니 생성
    public static void createCart(MemberEntity member) {
        CartEntity.builder()
                .member(member)
                .cartItems(new ArrayList<>())
                .build();
    }

    // 장바구니 상품 추가
    public void saveCart(CartItemEntity cartItem,
                         MemberEntity member) {
        this.member = member;
        this.totalCount = totalCount + cartItem.getCartCount();
        this.cartItems.add(cartItem);
    }

    // 장바구니 삭제시 totalCount 빼기
    public void minusCount(int count) {
        this.totalCount = Math.max(0, totalCount - count);  // 최소 0으로 유지
    }

    // 장바구니 총 개수 수정
    public void changeCount(int count) {
        this.totalCount = count;
    }


}
