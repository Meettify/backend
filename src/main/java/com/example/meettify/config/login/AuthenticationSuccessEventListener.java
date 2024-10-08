package com.example.meettify.config.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


/*
 *   worker  : 유요한
 *   work    : 로그인이 성공하면 LoginAttemptConfig를 호출해서 로그인 실패 횟수를 초기화하고 성공시킴
 *   date    : 2024/10/08
 * */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final LoginAttemptConfig loginAttemptConfig;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        log.info("UserDetails: {}", userDetails);
        loginAttemptConfig.loginSuccess(userDetails.getUsername());
    }
}
