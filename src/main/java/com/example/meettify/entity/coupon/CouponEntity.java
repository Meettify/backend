package com.example.meettify.entity.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "event")
@Builder
public class CouponEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    // 쿠폰의 만료일자
    private LocalDateTime expirationDate;
    // 쿠폰 할인 금액
    private Long salePrice;
    // 쿠폰 키
    private String couponKey;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private EventEntity event;

    // 쿠폰 엔티티 생성
    public static CouponEntity create(RequestCouponDTO coupon, String couponKey) {
        return CouponEntity.builder()
                .expirationDate(coupon.getExpirationDate())
                .salePrice(coupon.getDiscount())
                .couponKey(couponKey)
                .build();
    }
}
