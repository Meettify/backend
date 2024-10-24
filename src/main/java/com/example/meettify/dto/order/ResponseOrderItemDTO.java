package com.example.meettify.dto.order;

import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.entity.order.OrderItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ResponseOrderItemDTO {
    @Schema(description = "주문 상품 번호")
    private Long orderItemId;
    @Schema(description = "주문 상품 개수")
    private int orderCount;
    @Schema(description = "주문 상품 가격")
    private int orderPrice;
    @Schema(description = "주문 상품")
    private ResponseItemDTO item;

    public static ResponseOrderItemDTO changeDTO(OrderItemEntity orderItem) {
        ResponseItemDTO responseItem = ResponseItemDTO.changeDTO(orderItem.getItem());

        return ResponseOrderItemDTO.builder()
                .orderItemId(orderItem.getOrderItemId())
                .orderCount(orderItem.getOrderCount())
                .orderPrice(orderItem.getOrderPrice())
                .item(responseItem)
                .build();
    }
}
