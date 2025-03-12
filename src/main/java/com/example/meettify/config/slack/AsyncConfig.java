package com.example.meettify.config.slack;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/*
 *   worker : 유요한
 *   work   : 비종기 작업을 효율적으로 실행할 수 있도록 스레드풀을 설정
 *   date   : 2024/10/27
 * */
@Configuration
// Spring에서 비동기 메서드 실행을 활성화
// 이를 통해 애플리케이션에서 특정 메서드에 @Async를 추가하면 별도의 스레드에서 비동기로 실행
@EnableAsync
// 스케줄링된 작업(예: @Scheduled)을 활성화합니다. 이를 통해 특정 간격으로 작업을 실행하거나 예약 작업을 실행
@EnableScheduling
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    // Spring의 기본 비동기 스레드풀 대신 사용자 정의 스레드풀을 제공
    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Override
    public Executor getAsyncExecutor() {
        // 초기 생성할 스레드 개수
        executor.setCorePoolSize(5);
        // 최대 허용할 스레드 개수
        executor.setMaxPoolSize(10);
        // 스레드풀이 가득 찼을 때 대기할 작업 수
        executor.setQueueCapacity(100);
        // 생성되는 스레드 이름의 접두사
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
    // 애플리케이션 종료시, 스레드풀을 명시적으로 종료하여 리소스를 해제
    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown(); // 스레드 풀 종료
        log.debug("Async executor successfully shut down.");
    }
}
