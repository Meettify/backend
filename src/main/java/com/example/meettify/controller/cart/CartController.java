package com.example.meettify.controller.cart;

import com.example.meettify.dto.cart.*;
import com.example.meettify.exception.cart.CartException;
import com.example.meettify.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/carts")
public class CartController implements CartControllerDocs{
    private final CartService cartService;
    private final ModelMapper modelMapper;

    // 장바구니 등록
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addCart(@RequestBody RequestCartDTO cart,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            RequestCartServiceDTO serviceDTO = modelMapper.map(cart, RequestCartServiceDTO.class);
            ResponseCartDTO response = cartService.addCartItem(serviceDTO, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    // 장바구니 상품 삭제
    @Override
    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> removeCart(@PathVariable Long cartItemId,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            String response = cartService.deleteCartItem(cartItemId, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    // 장바구니 상품 수정
    @Override
    @PutMapping("/{cartId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateCart(@PathVariable Long cartId,
                                        @RequestBody List<UpdateCartItemDTO> carts,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            List<UpdateCartServiceDTO> serviceDTOS = carts.stream().map(cart -> modelMapper.map(cart, UpdateCartServiceDTO.class))
                    .collect(Collectors.toList());
            ResponseCartDTO response = cartService.updateCartItem(cartId, serviceDTOS, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    // 장바구니 조회
    @Override
    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getCart(@PathVariable Long cartId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("Getting cart with id: " + cartId);
            ResponseCartDTO response = cartService.cartDetail(cartId, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    // 장바구니 상품들 조회
    @Override
    @GetMapping("/cart-items")
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal UserDetails userDetails,
                                          Pageable page) {
        try {
            String email = userDetails.getUsername();
            Slice<ResponseCartItemDTO> cartItems = cartService.getCartItems(email, page);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("cartItems", cartItems.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", cartItems.getNumber() + 1);
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", cartItems.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", cartItems.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", cartItems.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", cartItems.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", cartItems.isLast());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }

    // 장바구니 번호 가져오기
    @Override
    @GetMapping("/id")
    public ResponseEntity<?> getCartId(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Long response = cartService.getCartId(email);
            log.info("Getting cart with id {} " , response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CartException(e.getMessage());
        }
    }
}
