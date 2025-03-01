package com.example.meettify.service.jwt;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlackListServiceImpl implements TokenBlackListService{
    private final RedisTemplate<String, Object> redisTemplate;
    private final String REDIS_BLACK_LIST_KEY = "tokenBlackList";

    // BlackList 내에 토큰을 추가합니다.
    @Override
    public void addTokenToList(String value) {
        redisTemplate.opsForSet().add(REDIS_BLACK_LIST_KEY, value);
    }

    // BlackList 내에 토큰이 존재하는지 여부를 확인
    @Override
    public boolean containToken(String value) {
        Boolean existsToken = redisTemplate.opsForSet().isMember(REDIS_BLACK_LIST_KEY, value);
        return Boolean.TRUE.equals(existsToken);
    }

    // BlackList 항목을 모두 조회
    @Override
    public Set<Object> getTokenBlackList() {
        Set<Object> blackToken = redisTemplate.opsForSet().members(REDIS_BLACK_LIST_KEY);
        return  blackToken;
    }

    // BlackList 내에서 항목을 제거
    @Override
    public void removeToken(String value) {
        redisTemplate.opsForSet().remove(REDIS_BLACK_LIST_KEY, value);
    }
}
