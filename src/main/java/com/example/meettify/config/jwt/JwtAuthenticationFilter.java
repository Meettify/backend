package com.example.meettify.config.jwt;

import com.example.meettify.service.jwt.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
// OncePerRequestFilter을 하는 이유는 한번만 작동하도록 하기 위해서 입니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_HEADER_KEY = "Authorization";
    private static final String REFRESH_TOKEN_HEADER_KEY = "x-refresh-token";
    private final JwtProvider jwtProvider;
    private final TokenBlackListService tokenBlackListService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        // 허용된 경로와 메서드 조합을 정의
        // 해당 경로는 토큰검사를 하지 않음
        if (PublicPathRegistry.isPublicPath(requestURI, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // request에서 JWT를 추출
        // 요청 헤더에서 토큰을 추출하는 역할
        // accessToken은 Authorization를 확인하여 접근
        // refreshToken은 x-refresh-token를 확인하여 접근
        String accessToken = resolveAccessToken(request);
        String refreshToken = resolveRefreshToken(request);
        String token = null;

        if (accessToken != null) {
            token = accessToken;
        }

        if (refreshToken != null) {
            token = refreshToken;
        }

        try {
            if (!StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
                throw new Exception("토큰 검증에 실패했습니다.");
            }

            // 블랙리스트에 포함된 토큰으로 접근하는 경우, 이를 막아줍니다.
            if (tokenBlackListService.containToken(token)) {
                throw new Exception("<< 경고 >>만료된 토큰으로 접근하려합니다!!!");
            }

            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
            // 여기까지 통과하면 토큰은 인증도 받았고 권한도 있다.
            Authentication authentication = jwtProvider.getAuthentication(token);
            // Spring Security의 SecurityContextHolder를 사용하여 현재 인증 정보를 설정합니다.
            // 이를 통해 현재 사용자가 인증된 상태로 처리됩니다.
            // 이렇게 저장하면 컨트롤러에서 토큰에서 정보를 가져와서 사용할 수 있습니다.
            log.info("인증에 성공했습니다. ");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("유효한 토큰이 없습니다.");
        }
        filterChain.doFilter(request, response);
    }

    // 토큰을 가져오기 위한 메소드
    // Authorization로 정의된 헤더 이름을 사용하여 토큰을 찾고
    // 토큰이 "Bearer "로 시작하거나 "Bearer "로 안온 것도 토큰 반환
    private String resolveAccessToken(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String token = request.getHeader(REFRESH_TOKEN_HEADER_KEY);

        return StringUtils.hasText(token) ? token : null;
    }
}
