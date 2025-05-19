package com.example.meettify.config.redis.cookie;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/*
 *   worker : 유요한
 *   work   : TimeUtils 클래스는 현재 시간부터 자정(내일 00:00:00)까지 남은 시간을 초 단위로 계산하는 유틸리티 클래스입니다
 *            주요 역할은 하루가 끝날 때까지 남은 시간을 정확하게 계산해주는 것입니다.
 *   date   : 2024/10/20
 * */
@Component
public class TimeUtils {
    public static long getRemainingTimeUntilMidnight() {
        // 현재 시간 가져옴
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간부터 내일 00:00:00(자정)까지의 남은 시간 계산
        Duration duration = Duration.between(now, now.plusDays(1).withHour(0).withMinute(0).withSecond(0));

        // 계산된 시간을 초로 변환
        return duration.getSeconds();
    }
}
