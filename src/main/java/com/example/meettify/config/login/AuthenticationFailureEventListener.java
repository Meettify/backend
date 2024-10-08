package com.example.meettify.config.login;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Service;

/*
 *   worker  : 유요한
 *   work    : 로그인이 실패하면 LoginAttemptConfig를 호출해서 로그인 실패 횟수를 업데이트
 *   date    : 2024/10/08
 * */
@Service
@RequiredArgsConstructor
public class AuthenticationFailureEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LoginAttemptConfig loginAttemptConfig;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        loginAttemptConfig.loginFailed(username);
    }
}
