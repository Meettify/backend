package com.example.meettify.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class RequestCouponDTO {
    @Schema(description = "쿠폰 만료일")
    @NotNull(message = "쿠폰 만료일을 등록해주세요.")
    private LocalDateTime expirationDate;

    @Schema(description = "쿠폰 수량")
    @NotNull(message = "쿠폰 수량을 등록해주세요.")
    private int quantity;

    @Schema(description = "할인율 퍼센트 or 할인 금액")
    @NotNull(message = "상품 할인율")
    private int discount;
}
