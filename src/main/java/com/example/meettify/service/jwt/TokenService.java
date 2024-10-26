package com.example.meettify.service.jwt;

import com.example.meettify.dto.jwt.TokenDTO;

public interface TokenService {
    // accessToken 재발급
    TokenDTO reissuanceAccessToken(String email, String refreshToken);
}
