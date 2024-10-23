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

    // 수정시 반환할 DTO
    public static ResponseCartDTO changeUpdateDTO(CartEntity cart,
                                                  String email,
                                                  List<CartItemEntity> cartItems) {
        // CartItemEntity 리스트를 ResponseCartItemDTO 리스트로 변환
        List<ResponseCartItemDTO> responseCartItems = cartItems.stream()
                .map(cartItem -> ResponseCartItemDTO.changeDTO(cartItem, cartItem.getItem()))  // 각 CartItemEntity를 DTO로 변환
                .collect(Collectors.toList());  // 결과를 리스트로 수집

        // 변환된 리스트를 포함하여 ResponseCartDTO를 반환
        return ResponseCartDTO.builder()
                .cartId(cart.getCartId())
                .memberEmail(email)
                .totalCount(cart.getTotalCount())
                .cartItems(responseCartItems)  // 변환된 DTO 리스트 추가
                .build();
    }

    // 조회시 반환해줄 DTO
    public static ResponseCartDTO changeDetailDTO(CartEntity cart) {
        List<ResponseCartItemDTO> cartItem = cart.getCartItems().stream()
                .map(ResponseCartItemDTO::changeDetailDTO)
                .collect(Collectors.toList());

        return ResponseCartDTO.builder()
                .cartId(cart.getCartId())
                .memberEmail(cart.getMember().getMemberEmail())
                .totalCount(cart.getTotalCount())
                .cartItems(cartItem)
                .build();
    }
}
