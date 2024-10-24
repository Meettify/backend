package com.example.meettify.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RequestOrderDTO {
    @Schema(description = "상품 번호")
    private Long itemId;
    @Schema(description = "상품 개수")
    @Min(value = 1, message = "상품의 최소 개수는 1개입니다.")
    private int itemCount;
}
