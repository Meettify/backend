package com.example.meettify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
// 테스트 환경 전용 설정 클래스
@TestConfiguration
class MeettifyApplicationTests {

    @Test
    void contextLoads() {
    }

}
