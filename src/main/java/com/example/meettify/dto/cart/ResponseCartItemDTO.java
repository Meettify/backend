package com.example.meettify.dto.cart;

import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.entity.cart.CartItemEntity;
import com.example.meettify.entity.item.ItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseCartItemDTO {
    @Schema(description = "장바구니 상품 번호")
    private Long cartItemId;
    @Schema(description = "장바구니 상품 가격")
    private int itemPrice;
    @Schema(description = "장바구니 상품 수량")
    private int itemCount;
    @Schema(description = "장바구니 상품")
    private ResponseItemDTO item;

    // 엔티티를 DTO로 변환
    public static ResponseCartItemDTO changeDTO(CartItemEntity cartItem,
                                                ItemEntity item) {
        ResponseItemDTO responseItem = ResponseItemDTO.changeDTO(cartItem.getItem());

        return ResponseCartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .itemPrice(item.getItemPrice())
                .itemCount(item.getItemCount())
                .item(responseItem)
                .build();
    }
}
