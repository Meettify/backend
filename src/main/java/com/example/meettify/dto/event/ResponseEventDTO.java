package com.example.meettify.dto.event;

import com.example.meettify.dto.coupon.RequestCouponDTO;
import com.example.meettify.dto.coupon.ResponseCouponDTO;
import com.example.meettify.entity.coupon.EventEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseEventDTO {
    @Schema(description = "이벤트 번호")
    private Long eventId;
    @Schema(description = "이벤트 제목을 입력해주세요")
    private String title;
    @Schema(description = "이벤트 내용에 대해서 입력해주세요")
    private String content;
    @Schema(description = "쿠폰에 대한 정보")
    private ResponseCouponDTO coupon;

    public static ResponseEventDTO change(EventEntity event) {
        return ResponseEventDTO.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .content(event.getContent())
                .coupon(ResponseCouponDTO.change(event.getCoupon()))
                .build();
    }
}
