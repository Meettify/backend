package com.example.meettify.dto.cart;

import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.item.status.ItemCartStatus;
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
    @Schema(description = "장바구니 상품 상태")
    private ItemCartStatus itemCartStatus; // CART_O or CART_X


    // 엔티티를 DTO로 변환
    public static ResponseCartItemDTO changeDTO(CartItemEntity cartItem,
                                                ItemEntity item) {
        ResponseItemDTO responseItem = ResponseItemDTO.changeDTO(cartItem.getItem());

        return ResponseCartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .itemPrice(item.getItemPrice())
                .itemCount(cartItem.getCartCount())
                .item(responseItem)
                .itemCartStatus(cartItem.getItemCartStatus())
                .build();
    }

    // 조회시 보내줄 DTO
    public static ResponseCartItemDTO changeDetailDTO(CartItemEntity cartItem) {
        return ResponseCartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .itemPrice(cartItem.getItem().getItemPrice())
                .itemCount(cartItem.getCartCount())
                .item(ResponseItemDTO.changeDTO(cartItem.getItem()))
                .itemCartStatus(cartItem.getItemCartStatus())
                .build();
    }
}
