package com.example.meettify.config.redis;

import com.example.meettify.config.cookie.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis에 저장된 특정 키의 값을 증가시키는 기능을 수행합니다
    // key에 해당하는 값(정수형)을 1만큼 증가시키는 것
    public void increaseData(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void addToSet(String key, Long communityId) {
        // 키가 없다면 (set이 없다면)
        if(!redisTemplate.hasKey(key)) {
            // set 생성
            redisTemplate.opsForSet().add(key, String.valueOf(communityId));
            // 만료 기간 설정
            redisTemplate.expire(key, TimeUtils.getRemainingTimeUntilMidnight(), TimeUnit.SECONDS);
        } else  {
            // 기존 키 값으로 된 set에 추가
            redisTemplate.opsForSet().add(key, String.valueOf(communityId));
        }
    }
    public boolean isExistInSet(String key, Long communityId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(communityId)));
    }

    // Redis에서 특정 키의 조회 수를 가져옵니다
    public Integer getViewCount(String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }

    // 조회 수를 0으로 초기화합니다
    public void resetViewCount(String key) {
        redisTemplate.opsForValue().set(key, 0);
    }
}
