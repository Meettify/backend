package com.example.meettify.controller.cart;

import com.example.meettify.dto.cart.RequestCartDTO;
import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.dto.cart.ResponseCartDTO;
import com.example.meettify.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> addCart(@RequestBody RequestCartDTO cart,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            RequestCartServiceDTO serviceDTO = modelMapper.map(cart, RequestCartServiceDTO.class);
            ResponseCartDTO response = cartService.addCartItem(serviceDTO, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("장바구니 담는 요청 실패 : " + e.getMessage());
        }
    }


}
