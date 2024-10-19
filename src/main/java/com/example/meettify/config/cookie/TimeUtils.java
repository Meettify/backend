package com.example.meettify.config.cookie;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeUtils {
    public static long getRemainingTimeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간부터 내일 00:00:00까지의 남은 시간 계산
        Duration duration = Duration.between(now, now.plusDays(1).withHour(0).withMinute(0).withSecond(0));

        // 계산된 시간을 초로 변환
        return duration.getSeconds();
    }
}
