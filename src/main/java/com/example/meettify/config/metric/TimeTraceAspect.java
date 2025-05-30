package com.example.meettify.config.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;


/*
 *   worker : 유요한
 *   work   : 시간 메트릭을 모으는 클래스
 *   date   : 2024/10/24
 * */
@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class TimeTraceAspect {
    private final MeterRegistry meterRegistry;

    @Pointcut("@annotation(com.example.meettify.config.metric.TimeTrace)")
    private void timeTracePointcut() {}

    @Around("timeTracePointcut()")
    public Object traceTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("start {}", joinPoint.toString());
        Timer timer = Timer.builder("response_time_ms")
                .register(meterRegistry);

        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            log.debug("Entering method: {}", joinPoint.getSignature().toShortString());
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            timer.record(stopWatch.getTotalTimeMillis(), TimeUnit.MILLISECONDS);
            log.debug(
                    "{} - Total time = {}s = {}ms",
                    joinPoint.getSignature().toShortString(),
                    stopWatch.getTotalTimeSeconds(),
                    stopWatch.getTotalTimeMillis());
        }
    }
}
