package com.example.meettify.entity.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.entity.item.ItemEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "item")
@Builder
public class CouponEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    // 쿠폰의 만료일자
    private LocalDateTime expirationDate;
    // 쿠폰 개수
    private int quantity;
    // 쿠폰 할인 금액
    private Long salePrice;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ItemEntity item;

    // 쿠폰 엔티티 생성
    public static CouponEntity create(RequestCouponDTO coupon) {
        return CouponEntity.builder()
                .expirationDate(coupon.getExpirationDate())
                .quantity(coupon.getQuantity())
                .build();
    }

    /*비즈니스 로직*/
    // 쿠폰 감소
    public void decrease() {
        this.quantity -= 1;
    }
    // 상품있는지 확인
    public boolean hasItem() {
        return item != null;
    }


}
