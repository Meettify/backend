package com.example.meettify.repository.coupon;

import com.example.meettify.entity.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
