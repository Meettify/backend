package com.example.meettify.config.login;

import com.example.meettify.dto.member.LoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter  extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // JSON 데이터를 읽어와서 User 객체로 변환
            LoginDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginDTO.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getMemberPw());

            // 인증된 객체 반환
            return this.getAuthenticationManager().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body");
        }
    }
}
