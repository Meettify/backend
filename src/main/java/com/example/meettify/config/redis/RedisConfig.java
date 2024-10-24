package com.example.meettify.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
 *   worker : 유요한
 *   work   : RedisConfig 클래스는 Spring Boot에서 Redis를 사용할 때 필요한 설정을 구성하는 클래스
 *   date   : 2024/10/20
 * */
@Configuration
public class RedisConfig {
    // RedisTemplate 설정: Redis에 데이터를 읽고 쓰기 위한 주요 인터페이스
    // RedisConnectionFactory 사용: Redis 서버와의 연결을 설정하는 공장 역할을 하는 객체
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 직렬화 설정
        // Redis의 키를 StringRedisSerializer를 사용하여 문자열로 직렬화합니다. Redis의 키는 보통 문자열 형태로 저장되므로 이 설정이 적절합니다.
        template.setKeySerializer(new StringRedisSerializer());
        // 값을 GenericJackson2JsonRedisSerializer로 직렬화합니다. 이 설정은 객체를 JSON 형식으로 직렬화하여 Redis에 저장할 수 있도록 해줍니다.
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
