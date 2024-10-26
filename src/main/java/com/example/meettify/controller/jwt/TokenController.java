package com.example.meettify.controller.jwt;

import com.example.meettify.dto.jwt.TokenDTO;
import com.example.meettify.service.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *   worker : 유요한
 *   work   : 게시글을 만들 때 필요한 값 즉, 제목과 내용만 받습니다.
 *   date   : 2024/10/09
 *   update ; 2024/10/26
 * */
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Log4j2
public class TokenController implements TokenControllerDocs{
    private final TokenService tokenService;

    // accessToken 재발급
    @Override
    @GetMapping("")
    public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestHeader("Authorization") String refreshToken) throws IllegalAccessException {
        try {
            String email = userDetails.getUsername();
            // 리프레시 토큰에서 "Bearer " 부분 제거
            refreshToken = refreshToken.replace("Bearer ", "");

            TokenDTO response = tokenService.reissuanceAccessToken(email, refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalAccessException(e.getMessage());
        }
    }
}
