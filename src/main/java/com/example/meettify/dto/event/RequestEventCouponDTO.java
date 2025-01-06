package com.example.meettify.dto.event;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RequestEventCouponDTO {
    @Schema(description = "이벤트 제목을 입력해주세요")
    private String title;
    @Schema(description = "이벤트 내용에 대해서 입력해주세요")
    private String content;
    @Schema(description = "쿠폰에 대한 정보")
    private RequestCouponDTO coupon;
}
