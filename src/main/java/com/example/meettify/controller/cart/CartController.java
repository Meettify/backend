package com.example.meettify.controller.cart;

import com.example.meettify.dto.cart.*;
import com.example.meettify.exception.cart.CartException;
import com.example.meettify.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
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
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            List<ResponseCartItemDTO> response = cartService.getCartItems(email);
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
