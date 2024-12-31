package com.example.meettify.dto.coupon;

import com.example.meettify.entity.coupon.CouponEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class ResponseCouponDTO {
    private Long couponId;
    private LocalDateTime expirationDate;
    private int quantity;
    @Schema(description = "할인율 퍼센트 or 할인 금액")
    @NotNull(message = "상품 할인율")
    private Long discount;

    public static ResponseCouponDTO change(CouponEntity coupon) {
        return ResponseCouponDTO.builder()
                .couponId(coupon.getCouponId())
                .expirationDate(coupon.getExpirationDate())
                .quantity(coupon.getQuantity())
                .discount(coupon.getSalePrice())
                .build();
    }
}
