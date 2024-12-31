package com.example.meettify.service.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;
import com.example.meettify.entity.coupon.CouponEntity;
import com.example.meettify.repository.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;

    @Override
    public ResponseCouponDTO createCoupon(RequestCouponDTO coupon) throws Exception {
        try {
            CouponEntity couponEntity = CouponEntity.create(coupon);
            CouponEntity saveCoupon = couponRepository.save(couponEntity);
            return ResponseCouponDTO.change(saveCoupon);
        } catch (Exception e) {
            throw new Exception("쿠폰 생성 실패");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseCouponDTO getCoupon(Long couponId) throws Exception {
        try {
            CouponEntity findCoupon = couponRepository.findById(couponId)
                    .orElseThrow();
            return ResponseCouponDTO.change(findCoupon);
        }catch (Exception e) {
            throw new Exception("쿠폰 생성 실패");
        }
    }

    @Override
    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }
}
