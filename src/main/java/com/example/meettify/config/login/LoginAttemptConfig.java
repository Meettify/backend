package com.example.meettify.config.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/*
*   worker  : 유요한
*   work    : 로그인 횟수를 카운트하고 캐시에 저장하는 클래스
*   date    : 2024/10/08
* */
@Slf4j
@Service
public class LoginAttemptConfig {
    private static final int MAX_ATTEMPTS = 5;
    // 사용자 username(email)이 Key, 로그인 실패 횟수가 value
    private LoadingCache<String, Integer> attemptsCache;

    // 생성하고 1일 지나면 만료되는 캐시를 생성
    public LoginAttemptConfig() {
        attemptsCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    // 성공하면 기존의 캐시를 비어줌
    public void loginSuccess(String email) {
        log.debug("로그인에 성공했습니다.");
        attemptsCache.invalidate(email);
    }

    // 로그인 실패하면 캐시 해당 사용자의 로그인 실패 횟수를 증가시키고 다시 캐시에 저장
    public void loginFailed(String email) {
        log.debug("로그인에 실패했습니다.");
        int failedAttemptsCount;

        try {
            failedAttemptsCount = attemptsCache.get(email);
        } catch (ExecutionException e) {
            failedAttemptsCount = 0;
        }
        failedAttemptsCount++;
        attemptsCache.put(email, failedAttemptsCount);
        // 현재 실패 횟수 로그 출력
        log.debug("현재 {}의 로그인 실패 횟수: {}", email, failedAttemptsCount);
    }

    // 정해진 로그링 실패 횟수를 초과하면 isBlocked가 true가 되어 해당 계정으로 로그인할 수 없습니다.
    public boolean isBlocked(String email) {
        try {
            return attemptsCache.get(email) >= MAX_ATTEMPTS;
        } catch (ExecutionException e) {
            log.warn("로그인 실패 횟수 가져오기 중 오류 발생", e);
            return false;
        }
    }
}
