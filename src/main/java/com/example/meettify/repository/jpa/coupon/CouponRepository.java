package com.example.meettify.repository.jpa.coupon;

import com.example.meettify.entity.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    // 이벤트에 속해 있는 쿠폰의 개수 카운트
    long countByEventEventId(Long eventId);
    // 이벤트에 속해 있는 쿠폰 조회
    CouponEntity findByEventEventId(Long eventId);
}
