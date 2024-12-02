package com.example.meettify.entity.cart;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

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
    @JsonIgnore
    private List<CartItemEntity> cartItems = new ArrayList<>();

    // 장바구니 생성
    public static CartEntity saveCart(MemberEntity member) {
        return CartEntity.builder()
                .member(member)
                .totalCount(0)
                .cartItems(new ArrayList<>())
                .build();
    }

    // 유저 엔티티 추가
    public void addMember(MemberEntity member) {
        this.member = member;
    }

    // 장바구니 삭제시 totalCount 빼기
    public void minusCount(int count) {
        this.totalCount = Math.max(0, totalCount - count);  // 최소 0으로 유지
    }

    // 장바구니 등록시 총 개수
    public void plusCount(int count) {
        this.totalCount = this.totalCount + count;
    }

    // 장바구니 수정시 총 개수
    public void updateCount(int count) {
        this.totalCount = count;
    }

}
