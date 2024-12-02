package com.example.meettify.controller.cart;

import com.example.meettify.dto.cart.RequestCartDTO;
import com.example.meettify.dto.cart.UpdateCartItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "장바구니", description = "장바구니 API")
public interface CartControllerDocs {
    @Operation(summary = "장바구니에 상품담기", description = "장바구니에 상품 담는 API")
    ResponseEntity<?> addCart(RequestCartDTO cart, UserDetails userDetails);

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니 상품 삭제하는 API")
    ResponseEntity<?> removeCart(Long cartItemId, UserDetails userDetails);

    @Operation(summary = "장바구니 상품 수정", description = "장바구니 상품 수정하는 API")
    ResponseEntity<?> updateCart(Long cartId, List<UpdateCartItemDTO> cart, UserDetails userDetails);

    @Operation(summary = "장바구니 조회", description = "장바구니 조회 API")
    ResponseEntity<?> getCart(Long cartId, UserDetails userDetails);

    @Operation(summary = "장바구니 상품 조회", description = "장바구니 상품에 뭐가 있는지 알기 위한 API")
    ResponseEntity<?> getCartItems(UserDetails userDetails);

    @Operation(summary = "장바구니 번호 조회", description = "장바구니 번호 반환 API")
    ResponseEntity<?> getCartId(UserDetails userDetails);
}
