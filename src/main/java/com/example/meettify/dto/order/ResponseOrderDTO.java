package com.example.meettify.dto.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.entity.order.OrderEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ResponseOrderDTO {
    @Schema(description = "주문 번호")
    private Long orderId;
    @Schema(description = "주문 주소")
    private AddressDTO orderAddress;
    @Schema(description = "주문 총 상품 가격")
    private int orderTotalPrice;
    @Schema(description = "주문 상품")
    private List<ResponseOrderItemDTO> orderItems = new ArrayList<>();

    public static ResponseOrderDTO changeDTO(OrderEntity order, AddressDTO address) {
        List<ResponseOrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(ResponseOrderItemDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderAddress(address)
                .orderTotalPrice(order.getTotalPrice())
                .orderItems(orderItems)
                .build();
    }
}
