package com.example.meettify.config.redis.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/*
 *   worker : 유요한
 *   work   : CookieUtils 클래스는 쿠키를 처리하는 유틸리티로, 주로 클라이언트 요청에서 viewCountCookie라는
 *            이름의 쿠키를 확인하고 없으면 새로 생성하여 응답에 추가하는 역할
 *   date   : 2024/10/20
 * */
@Component
public class CookieUtils {
    // 클라이언트가 보낸 요청에서 viewCountCookie라는 이름의 쿠키를 확인하고, 해당 쿠키가 있으면 그 값을 반환
    public static  String getViewCountCookieValue(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            Optional<Cookie> viewCountCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("viewCountCookie"))
                    .findFirst();

            if(viewCountCookie.isPresent()){
                return viewCountCookie.get().getValue();
            }
        }
        return createAndAddViewCookie(response);
    }
    // UUID로 무작위 문자열을 생성하여 새로운 viewCountCookie를 만들고, 클라이언트 응답에 추가합니다.
    private static String createAndAddViewCookie(HttpServletResponse response) {
        String randomCookieValue = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from("viewCountCookie", randomCookieValue)
                // 쿠키가 모든 경로에서 사용될 수 있도록 설정합니다.
                .path("/")
                // 쿠키가 크로스 사이트 요청에서 전송될 수 있도록 설정
                .sameSite("None")
                // 클라이언트 측 JavaScript에서 쿠키에 접근할 수 없도록 하여 보안을 강화합니다.
                .httpOnly(true)
                // HTTPS 연결에서만 쿠키를 전송하도록 보장합니다.
                .secure(true)
                // 쿠키가 24시간 동안 유지되도록 설정
                .maxAge(60 * 60 * 24)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return randomCookieValue;
    }
}
