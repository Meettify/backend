package com.example.meettify.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.channels.AcceptPendingException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
// OncePerRequestFilter을 하는 이유는 한번만 작동하도록 하기 위해서 입니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();  // AntPathMatcher 사용



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest httpServletRequest = request;
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        // 허용된 경로와 메서드 조합을 정의
        // 해당 경로는 토큰검사를 하지 않음
        Map<String, List<String>> publicPaths = new HashMap<>();
        publicPaths.put("/api/v1/members/**", Arrays.asList("GET", "POST"));
        publicPaths.put("/api/v1/notice/**", List.of("GET"));
        publicPaths.put("/api/v1/items/**", List.of("GET"));
        publicPaths.put("/api/v1/community/**", List.of("GET"));
        publicPaths.put("/", List.of("GET"));
        publicPaths.put("/swagger-resources/**", List.of("GET"));
        publicPaths.put("/swagger-ui/**", List.of("GET"));
        publicPaths.put("/v3/api-docs/**", List.of("GET"));
        publicPaths.put("/api/swagger-config", List.of("GET"));
        publicPaths.put("/api/logistics", List.of("GET"));
        publicPaths.put("/monitor/**", List.of("GET"));
        publicPaths.put("/prometheus", List.of("GET"));
        publicPaths.put("/grafana", List.of("GET"));

        // 허용된 경로와 메서드 조합을 통해 검증
        for (Map.Entry<String, List<String>> entry : publicPaths.entrySet()) {
            String pathPattern = entry.getKey();
            List<String> allowedMethods = entry.getValue();

            // AntPathMatcher를 사용한 경로 매칭 및 메서드 검증
            if (pathMatcher.match(pathPattern, requestURI) && allowedMethods.contains(method)) {
                // 허용된 경로 및 메서드에 해당하면 필터 제외
                filterChain.doFilter(request, response);
                return;
            }
        }

        // request에서 JWT를 추출
        // 요청 헤더에서 토큰을 추출하는 역할
        String token = resolveToken(httpServletRequest);
        log.info("token : " + token);

        // 토큰이 존재하고 토큰 유효성 검사를 통과하면 true
        if(StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
            // 여기까지 통과하면 토큰은 인증도 받았고 권한도 있다.
            Authentication authentication = jwtProvider.getAuthentication(token);
            // Spring Security의 SecurityContextHolder를 사용하여 현재 인증 정보를 설정합니다.
            // 이를 통해 현재 사용자가 인증된 상태로 처리됩니다.
            // 이렇게 저장하면 컨트롤러에서 토큰에서 정보를 가져와서 사용할 수 있습니다.
            log.info("인증에 성공했습니다. ");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.error("유효한 토큰이 없습니다.");
        }
        filterChain.doFilter(request, response);
    }

    // 토큰을 가져오기 위한 메소드
    // Authorization로 정의된 헤더 이름을 사용하여 토큰을 찾고
    // 토큰이 "Bearer "로 시작하거나 "Bearer "로 안온 것도 토큰 반환
    private String resolveToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        // 토큰이 포함하거나 Bearer 로 시작하면 true
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        } else if(StringUtils.hasText(token)) {
            return token;
        } else {
            return null;
        }
    }
}
