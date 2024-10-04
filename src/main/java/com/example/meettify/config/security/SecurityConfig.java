package com.example.meettify.config.security;

import com.example.meettify.config.jwt.JwtProvider;
import com.example.meettify.config.jwt.JwtSecurityConfig;
import com.example.meettify.config.oauth.OAuth2FailHandler;
import com.example.meettify.config.oauth.OAuth2SuccessHandler;
import com.example.meettify.config.oauth.PrincipalOAuthUserService;
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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        // API 권한 설정
//                        .requestMatchers("/","/**").permitAll()
                        .requestMatchers("/api/v1/members/**").permitAll() // 모든 멤버 관련 요청 허용
                        .requestMatchers(HttpMethod.PUT, "/api/v1/members/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/{memberId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/notice/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notice/{noticeId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/notice/{noticeId}").hasRole("ADMIN")
                        .requestMatchers("/api/v1/notice/*").permitAll()
                        .requestMatchers("/api/v1/items/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/items/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/items/{itemId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/items/{itemId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart/{cartId}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart/{cartId}").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/api/v1/meets/**").permitAll()
                                .requestMatchers("/api/v1/meetBoards/**").permitAll()


                                // Swagger 리소스에 대한 접근 허용
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-config").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/logistics").permitAll()

                );

        // JWT Configuration
        http.apply(new JwtSecurityConfig(jwtProvider));

        // Exception handling for authentication/authorization issues
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler())
        );

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
    public WebSecurityCustomizer securityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/v3/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");
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
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // 허용할 Origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 메서드
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정
        return source;
    }
}
