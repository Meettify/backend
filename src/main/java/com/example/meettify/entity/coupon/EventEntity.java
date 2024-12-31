package com.example.meettify.entity.coupon;

import com.example.meettify.config.auditing.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class EventEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private String title;
    private String content;
    private LocalDateTime openAt;
    @OneToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity coupon;

    // 쿠폰 추가
    public void addCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }
}
