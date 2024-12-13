package com.example.meettify.config.redis;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/*
 *   worker : 유요한
 *   work   : 성능 최적화하거나 자주 쓰이는 데이터들을 레디스에 저장하고 사용하기 위한 클래스
 *   date   : 2024/12/02
 * */
@Configuration
// SpringBoot의 캐싱 설정을 활성화
@EnableCaching
public class RedisCacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                // Redis에 key를 저장할 때 String으로 직렬화하여 저장
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer())
                )
                // Redis에 value를 저장할 때 JSON으로 직렬화해서 저장
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new Jackson2JsonRedisSerializer<>(Object.class)
                        )
                )
                // 데이터의 만료기간(TTL) 설정
                .entryTtl(Duration.ofDays(1));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
