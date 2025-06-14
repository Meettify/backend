package com.example.meettify.service.cart;

import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.dto.cart.ResponseCartDTO;
import com.example.meettify.dto.cart.ResponseCartItemDTO;
import com.example.meettify.dto.cart.UpdateCartServiceDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CartService {
    // 장바구니에 상품 추가
    ResponseCartDTO addCartItem(RequestCartServiceDTO cart, String email);
    // 잡바구니 상품 삭제
    String deleteCartItem(Long itemId, String email);
    // 장바구니 수정
    ResponseCartDTO updateCartItem(Long cartId, List<UpdateCartServiceDTO> carts, String email);
    // 장바구니 조회
    ResponseCartDTO cartDetail(Long cartId, String email);
    // 장바구니 상태 보기
    Slice<ResponseCartItemDTO> getCartItems(String email, Pageable page);
    // 장바구니 번호 반환
    Long getCartId(String email);
}
