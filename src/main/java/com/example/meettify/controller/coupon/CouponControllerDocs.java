package com.example.meettify.controller.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "쿠폰", description = "쿠폰 생성 컨트롤러")
public interface CouponControllerDocs {
    @Operation(summary = "쿠폰 생성", description = "쿠폰 생성 api")
    ResponseEntity<?> createCoupon(RequestCouponDTO coupon) throws Exception;
    @Operation(summary = "쿠폰 조회", description = "쿠폰 조회 api")
    ResponseEntity<?> getCoupon(Long couponId) throws Exception;
    @Operation(summary = "쿠폰 삭제", description = "쿠폰 삭제 api")
    ResponseEntity<?> deleteCoupon(Long couponId) throws Exception;
}
