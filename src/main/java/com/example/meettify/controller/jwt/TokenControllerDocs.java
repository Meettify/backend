package com.example.meettify.controller.jwt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "토큰", description = "토큰 API")
public interface TokenControllerDocs {
    @Operation(summary = "재발급", description = "accessToken 재발급 API")
    ResponseEntity<?> getAccessToken(UserDetails userDetails,  String refreshToken) throws IllegalAccessException;
}
