package com.example.meettify.service.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;

public interface CouponService {
    // 쿠폰 생성
    ResponseCouponDTO createCoupon(RequestCouponDTO coupon, Long eventId) throws Exception;
    // 쿠폰 조회
    ResponseCouponDTO getCoupon(Long couponId) throws Exception;
    // 쿠폰 삭제
    void deleteCoupon(Long couponId);
    // 쿠폰 발급
    ResponseCouponDTO issueCoupon(Long eventId, String email);
}
