package com.example.meettify.config.slack;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@Log4j2
public class AsyncConfig implements AsyncConfigurer {
    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Override
    public Executor getAsyncExecutor() {
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown(); // 스레드 풀 종료
        log.info("Async executor successfully shut down.");
    }
}
