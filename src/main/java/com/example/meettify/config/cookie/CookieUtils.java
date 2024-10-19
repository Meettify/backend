package com.example.meettify.config.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class CookieUtils {
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

    private static String createAndAddViewCookie(HttpServletResponse response) {
        String randomCookieValue = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from("viewCountCookie", randomCookieValue)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .maxAge(60 * 60 * 24)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return randomCookieValue;
    }
}
