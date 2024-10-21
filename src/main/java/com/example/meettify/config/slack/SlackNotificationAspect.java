package com.example.meettify.config.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/*
*   worker  : 유요한
*   work    : 이 클래스는 HttpServletRequest 정보와 예외 정보를 수집하여 슬랙 메시지를 구성하고,
*             ThreadPoolTaskExecutor를 사용해 비동기적으로 메시지를 보냅니다.
*   date    : 2024/10/18
* */

@Aspect
@Component
@Profile(value = {"prod"})
@RequiredArgsConstructor
@Log4j2
public class SlackNotificationAspect {
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final SlackUtil slackUtil;

    // @Around 어드바이스를 사용하여, @SlackNotification 어노테이션이 붙은 메서드를 감싸며 에러가 발생할 경우 슬랙으로 메시지를 전송하는 역할
    @Around("@annotation(com.example.meettify.config.slack.SlackNotification) && args(request, e)")
    public void slackNotificate(ProceedingJoinPoint proceedingJoinPoint, HttpServletRequest request,
                                Exception e) throws Throwable {

        proceedingJoinPoint.proceed();

        //HttpServletRequest를 RequestInfo라는 DTO에 복사
        RequestInfo requestInfo = new RequestInfo(request);

        // 비동기 알림 전송
        threadPoolTaskExecutor.execute(() -> {
            slackUtil.sendAlert(e, requestInfo);
        });
    }


}
