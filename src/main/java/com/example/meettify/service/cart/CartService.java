package com.example.meettify.service.cart;

import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.dto.cart.ResponseCartDTO;

public interface CartService {
    // 장바구니에 상품 추가
    ResponseCartDTO addCartItem(RequestCartServiceDTO cart, String email);
    // 잡바구니 상품 삭제
    String deleteCartItem(Long cartItemId, String email);
}
