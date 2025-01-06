package com.example.meettify.controller.event;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;
import com.example.meettify.service.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController implements CouponControllerDocs{
    private final CouponService couponService;

    // 쿠폰 조회
    @Override
    @GetMapping("/{couponId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getCoupon(@PathVariable Long couponId) throws Exception {
        try {
            ResponseCouponDTO response = couponService.getCoupon(couponId);
            log.info("response {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // 쿠폰 삭제
    @Override
    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long couponId) throws Exception {
        try {
            couponService.deleteCoupon(couponId);
            return ResponseEntity.ok("쿠폰이 삭제되었습니다.");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
