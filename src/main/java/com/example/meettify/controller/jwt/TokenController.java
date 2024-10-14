package com.example.meettify.controller.jwt;

import com.example.meettify.dto.jwt.TokenDTO;
import com.example.meettify.service.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Log4j2
public class TokenController implements TokenControllerDocs{
    private final TokenService tokenService;

    // accessToken 재발급
    @Override
    @GetMapping("")
    public ResponseEntity<?> getAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            TokenDTO response = tokenService.reissuanceAccessToken(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
