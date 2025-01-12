package com.example.meettify.dto.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.entity.order.OrderEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
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
    @Builder.Default
    private List<ResponseOrderItemDTO> orderItems = new ArrayList<>();
    @Schema(description = "주문 랜덤 번호")
    private String orderUid;
    @Schema(description = "주문 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime orderTime;
    @Schema(description = "결제 상태")
    private PayStatus payStatus;

    // 주문 정보를 생성할 때 DTO로 변환해줄 메서드
    public static ResponseOrderDTO changeDTO(OrderEntity order, AddressDTO address, String orderUid) {
        List<ResponseOrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(ResponseOrderItemDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderAddress(address)
                .orderTotalPrice(order.getTotalPrice())
                .orderItems(orderItems)
                .orderUid(orderUid)
                .orderTime(order.getRegTime())
                .payStatus(order.getPayStatus())
                .build();
    }

    // 조회한 것을 DTO로 변환해줄 메서드
    public static ResponseOrderDTO viewChangeDTO(OrderEntity order) {
        List<ResponseOrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(ResponseOrderItemDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseOrderDTO.builder()
                .orderId(order.getOrderId())
                .orderAddress(AddressDTO.changeDTO(order.getAddress()))
                .orderTotalPrice(order.getTotalPrice())
                .orderItems(orderItems)
                .orderTime(order.getRegTime())
                .payStatus(order.getPayStatus())
                .build();
    }

    // 주문 정보 생성
    public static ResponseOrderDTO createDTO(List<ResponseOrderItemDTO> orderItems,
                                             AddressDTO address,
                                             String orderUid,
                                             int orderTotalPrice) {
        return ResponseOrderDTO.builder()
                .orderAddress(address)
                .orderTotalPrice(orderTotalPrice)
                .orderItems(orderItems)
                .orderUid(orderUid)
                .payStatus(PayStatus.PAY_X)
                .build();
    }
}
