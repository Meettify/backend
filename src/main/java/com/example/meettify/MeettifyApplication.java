package com.example.meettify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling   // 스케쥴링 기능 활성화
@EnableFeignClients(basePackages = "com.example.meettify") // Feign Client의 패키지를 스캔
public class MeettifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeettifyApplication.class, args);
    }

}
