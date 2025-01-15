package com.example.meettify.dto.event;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateEventDTO {
    @Schema(description = "이벤트 제목을 입력해주세요")
    private String title;
    @Schema(description = "이벤트 내용에 대해서 입력해주세요")
    private String content;
    @Schema(description = "쿠폰 개수")
    private int couponCount;
}
