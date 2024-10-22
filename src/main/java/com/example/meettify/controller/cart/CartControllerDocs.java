package com.example.meettify.controller.cart;

import com.example.meettify.dto.cart.RequestCartDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "장바구니", description = "장바구니 API")
public interface CartControllerDocs {
    @Operation(summary = "장바구니 생성", description = "장바구니 생성하는 API")
    ResponseEntity<?> addCart(RequestCartDTO cart, UserDetails userDetails);
}
