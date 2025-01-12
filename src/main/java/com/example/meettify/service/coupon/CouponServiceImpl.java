package com.example.meettify.service.coupon;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;
import com.example.meettify.entity.coupon.CouponEntity;
import com.example.meettify.entity.coupon.EventEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.coupon.CouponException;
import com.example.meettify.repository.jpa.coupon.CouponRepository;
import com.example.meettify.repository.jpa.event.EventRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CouponServiceImpl implements CouponService{
    private final CouponRepository couponRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    // 쿠폰 생성
    @Override
    public ResponseCouponDTO createCoupon(RequestCouponDTO coupon, Long eventId) {
        try {
            // 쿠폰키 생성
            String couponKey = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            // 쿠폰 엔티티 생성
            CouponEntity couponEntity = CouponEntity.create(coupon, couponKey);
            // 쿠폰 디비에 저장
            CouponEntity saveCoupon = couponRepository.save(couponEntity);
            // 이벤트 조회
            EventEntity findEvent = eventRepository.findById(eventId)
                    .orElseThrow(() -> new BoardException("해당 이벤트는 존재하지 않습니다."));
            // 이벤트에 쿠폰 등록
            findEvent.addCoupon(saveCoupon);

            return ResponseCouponDTO.change(saveCoupon);
        } catch (Exception e) {
            throw new CouponException("쿠폰 생성 실패");
        }
    }

    // 쿠폰 조회
    @Override
    @Transactional(readOnly = true)
    public ResponseCouponDTO getCoupon(Long couponId) {
        try {
            CouponEntity findCoupon = couponRepository.findById(couponId)
                    .orElseThrow();
            return ResponseCouponDTO.change(findCoupon);
        }catch (Exception e) {
            throw new CouponException("쿠폰 생성 실패");
        }
    }

    // 쿠폰 삭제
    @Override
    public void deleteCoupon(Long couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseCouponDTO issueCoupon(Long eventId, String email) {
        try {
            // 쿠폰 조회
            CouponEntity findCoupon = couponRepository.findByEventEventId(eventId);

            // 이벤트에 속한 쿠폰 갯수 카운트
            long count = couponRepository.countByEventEventId(eventId);

            if(count > findCoupon.getQuantity()) {
                throw new CouponException("쿠폰은 "+findCoupon.getQuantity()+"개를 넘을 수 없습니다.");
            }
            return null;
        }catch (Exception e) {
            throw new CouponException("쿠폰 생성 실패");
        }
    }
}
