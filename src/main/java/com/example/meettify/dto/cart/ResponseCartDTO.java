package com.example.meettify.dto.cart;


import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.cart.CartItemEntity;
import com.example.meettify.entity.item.ItemEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseCartDTO {
    @Schema(description = "장바구니 번호")
    private Long cartId;
    @Schema(description = "장바구니가 속한 이메일")
    private String memberEmail;
    @Schema(description = "장바구니에 담은 총 개수")
    private int totalCount;
    @Schema(description = "장바구니 상품들")
    @Builder.Default
    private List<ResponseCartItemDTO> cartItems = new ArrayList<>();


    // 엔티티를 DTO로 변환
    public static ResponseCartDTO changeDTO(CartEntity cart,
                                            String email,
                                            ItemEntity item) {

        List<CartItemEntity> cartItems = cart.getCartItems() != null ? cart.getCartItems() : new ArrayList<>();
        List<ResponseCartItemDTO> responseCartItem = cartItems.stream()
                .map(cartItem -> ResponseCartItemDTO.changeDTO(cartItem, item))
                .collect(Collectors.toList());

        return ResponseCartDTO.builder()
                .cartId(cart.getCartId())
                .memberEmail(email)
                .cartItems(responseCartItem)
                .build();
    }
}
