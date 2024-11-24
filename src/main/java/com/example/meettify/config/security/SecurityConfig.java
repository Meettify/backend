package com.example.meettify.config.security;

import com.example.meettify.config.jwt.JwtAuthenticationFilter;
import com.example.meettify.config.jwt.JwtProvider;
import com.example.meettify.config.oauth.OAuth2FailHandler;
import com.example.meettify.config.oauth.OAuth2SuccessHandler;
import com.example.meettify.config.oauth.PrincipalOAuthUserService;
import com.example.meettify.config.slack.SlackUtil;
import com.example.meettify.exception.jwt.JwtAccessDeniedHandler;
import com.example.meettify.exception.jwt.JwtAuthenticationEntryPoint;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailHandler oAuth2FailHandler;
    private final PrincipalOAuthUserService principalOAuthUserService;
    private final SlackUtil slackUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .csrf(csrf -> csrf.disable())  // Disable CSRF as you're using JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        http
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint(slackUtil))
                        .accessDeniedHandler(new JwtAccessDeniedHandler(slackUtil))
                )
                .authorizeHttpRequests(auth -> auth
                        // API 권한 설정
                        .requestMatchers("/", "/**").permitAll()

                        // 유저
                        .requestMatchers("/api/v1/members/**").permitAll() // 모든 멤버 관련 요청 허용
                        .requestMatchers(HttpMethod.PUT, "/api/v1/members/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/{memberId}").hasAnyRole("USER", "ADMIN")

                        // 공지사항
                        .requestMatchers("/api/v1/notice/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/notice").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notice/{noticeId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/notice/{noticeId}").hasRole("ADMIN")

                        // 문의글
                        .requestMatchers(HttpMethod.POST,"/api/v1/questions").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/questions/{questionId}").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/questions/{questionId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/questions/{questionId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/questions/my-questions").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/questions/count-my-questions").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/questions/count-questions").hasRole("ADMIN")


                        // 상품
                        .requestMatchers("/api/v1/items/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/items").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/items/{itemId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/items/{itemId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/items/item-list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/items/confirm/{itemId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/items/count-items").hasRole("ADMIN")


                        // 커뮤니티
                        .requestMatchers("/api/v1/community/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/community").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/community/{communityId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/community/{communityId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/my-community").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/count-my-community").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/count-community").hasRole("ADMIN")

                        // 댓글
                        .requestMatchers("/api/v1/{communityId}/comment/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/{communityId}/comment").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/{communityId}/comment/{commentId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/{communityId}/comment/{commentId}").hasAnyRole("USER", "ADMIN")

                        // 장바구니
                        .requestMatchers(HttpMethod.GET, "/api/v1/carts/{cartId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/carts").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/carts/{cartId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/carts/{cartItemId}").hasAnyRole("USER", "ADMIN")

                        // 주문하기
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/tempOrder").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/my-order").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/count-my-order").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/count-order").hasRole("ADMIN")

                        // 결제하기
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/verify").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/confirm").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/iamport/cancel").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/toss/cancel").hasRole("USER")

                        //모임
                        .requestMatchers("/api/v1/meets/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/{meetId}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/{meetId}/role").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/{meetId}/members").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/myMeet").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/meets/admin/{meetId}/{meetMemberId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/meets/{meetId}/{meetMemberId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/meets/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/meets/{meetId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/meets/{meetId}/members/edit-permission").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/meets/{meetId}/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/meets/{meetId}/members").hasAnyRole("USER", "ADMIN")


                        .requestMatchers("/api/v1/meetBoards/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/meetBoards/list/{meetId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/meetBoards/{meetBoardId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/meetBoards/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/meetBoards/{meetId}/{meetBoardId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/v1/meetBoards/{meetBoardId}").hasAnyRole("USER", "ADMIN")


                        .requestMatchers("/api/v1/meetBoards/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/meetBoards/meets/{meetId}/boards/{meetBoardId}/comments").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/meetBoards/{meetBoardCommentId}").hasAnyRole("USER", "ADMIN")

                        // 검색
                        .requestMatchers("/api/v1/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/search").permitAll()

                        // 관리자
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/members").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/questions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/{questionId}/answer").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/admin/{questionId}/answer/{answerId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/{questionId}/answer/{answerId}").hasRole("ADMIN")

                        // 알림
                        .requestMatchers("/api/v1/notify/subscribe").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/notify/{notification-id}").hasAnyRole("USER", "ADMIN")
                        // SSE 연결을 위해
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger 리소스에 대한 접근 허용
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-config").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/logistics").permitAll()

                        // prometheus & grafana
                        .requestMatchers("/monitor/**").permitAll()
                        .requestMatchers("/prometheus").permitAll()
                        .requestMatchers("/grafana").permitAll());

        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);


        // OAuth2 Login configuration
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(principalOAuthUserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailHandler)
        );

        // Enable actuator access without authentication
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/**").permitAll());

        return http.build();
    }


    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customize() {
        return p -> {
            p.setOneIndexedParameters(true);    // 1-based pagination
            p.setMaxPageSize(10);               // Maximum 10 items per page
        };
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Spring Boot 2.4+부터는 setAllowedOrigins 대신 setAllowedOriginPatterns를 사용하는 것이 권장
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "do2867lf6anbu.cloudfront.net")); // 허용할 Origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정
        return source;
    }
}
