//package com.example.meettify.service.coupon;
//
//import com.example.meettify.dto.coupon.RequestCouponDTO;
//import com.example.meettify.dto.coupon.ResponseCouponDTO;
//import com.example.meettify.dto.event.ResponseEventDTO;
//import com.example.meettify.entity.coupon.CouponEntity;
//import com.example.meettify.entity.coupon.EventEntity;
//import com.example.meettify.repository.coupon.CouponRepository;
//import com.example.meettify.repository.event.EventRepository;
//import com.example.meettify.service.event.EventServiceImpl;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class) // Mockito 사용
//@Transactional
//class CouponServiceImplTest {
//    @InjectMocks
//    private CouponServiceImpl couponService;
//    @Mock
//    private CouponRepository couponRepository;
//    @Mock
//    private EventRepository eventRepository;
//
//    private RequestCouponDTO coupon() {
//        return RequestCouponDTO.builder()
//                .expirationDate(LocalDateTime.now().plusDays(7))    // 7일 뒤 만료
//                .discount(25L)      // 25% 할인
//                .quantity(100)      // 수량 100개
//                .build();
//    }
//
//    @Test
//    void 쿠폰생성() throws Exception {
//        // Arrange: Mock 동작 정의
//        RequestCouponDTO requestCoupon = coupon();
//
//        // Mock 이벤트 엔티티 생성
//        EventEntity mockEvent = EventEntity.builder()
//                .eventId(1L)
//                .title("할인 쿠폰")
//                .content("쿠폰 이벤트")
//                .build();
//
//        // Mock 쿠폰 엔티티 생성
//        CouponEntity mockCoupon = CouponEntity.builder()
//                .couponId(1L)
//                .expirationDate(requestCoupon.getExpirationDate())
//                .quantity(requestCoupon.getQuantity())
//                .salePrice(requestCoupon.getDiscount())
//                .build();
//
//        // Mock 동작 정의
//        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent)); // 이벤트 조회 Mock
//        when(couponRepository.countByEventEventId(1L)).thenReturn(0L); // 쿠폰 수 Mock
//        when(couponRepository.save(any(CouponEntity.class))).thenReturn(mockCoupon); // 쿠폰 저장 Mock
//
//        // Act: 테스트 실행
//        ResponseCouponDTO createdCoupon = couponService.createCoupon(requestCoupon, 1L);
//
//        // Assert: 결과 검증
//        assertNotNull(createdCoupon, "생성된 쿠폰은 null이 아니어야 합니다.");
//        assertEquals(requestCoupon.getDiscount(), createdCoupon.getDiscount(), "할인율이 일치해야 합니다.");
//        assertEquals(requestCoupon.getQuantity(), createdCoupon.getQuantity(), "쿠폰 수량이 일치해야 합니다.");
//        assertEquals(requestCoupon.getExpirationDate(), createdCoupon.getExpirationDate(), "만료일이 일치해야 합니다.");
//
//        // Verify: Mock 호출 검증
//        verify(eventRepository).findById(1L);
//        verify(couponRepository).countByEventEventId(1L);
//        verify(couponRepository).save(any(CouponEntity.class));
//    }
//
//    @Test
//    void 여러명응모() throws InterruptedException {
//        // Arrange: Mock 동작 정의
//        RequestCouponDTO requestCoupon = coupon();
//
//        // Mock 이벤트 엔티티 생성
//        EventEntity mockEvent = EventEntity.builder()
//                .eventId(1L)
//                .title("할인 쿠폰")
//                .content("쿠폰 이벤트")
//                .build();
//
//        // Mock 쿠폰 엔티티 생성
//        CouponEntity mockCoupon = CouponEntity.builder()
//                .couponId(1L)
//                .expirationDate(requestCoupon.getExpirationDate())
//                .quantity(requestCoupon.getQuantity())
//                .salePrice(requestCoupon.getDiscount())
//                .build();
//
//        // Mock 동작 정의
//        when(eventRepository.findById(1L)).thenReturn(Optional.of(mockEvent)); // 이벤트 조회 Mock
//        when(couponRepository.countByEventEventId(1L)).thenReturn(0L); // 쿠폰 수 Mock
//        when(couponRepository.save(any(CouponEntity.class))).thenReturn(mockCoupon); // 쿠폰 저장 Mock
//
//        int threadCount = 1000;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            long userId = i;
//            executorService.submit(() -> {
//                try {
//                    couponService.createCoupon(requestCoupon, 1L);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//        latch.await();
//
//        // Verify: 동시성 요청 후 Mock 호출 검증
//        verify(eventRepository, times(threadCount)).findById(1L);
//        verify(couponRepository, times(threadCount)).countByEventEventId(1L);
//        verify(couponRepository, times(threadCount)).save(any(CouponEntity.class));
//    }
//}