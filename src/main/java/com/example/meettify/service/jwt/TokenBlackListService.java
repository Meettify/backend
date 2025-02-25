package com.example.meettify.service.jwt;

import java.util.List;
import java.util.Set;

public interface TokenBlackListService {
    // Redis key-value 형태로 리스트 추가
    void addTokenToList(String value);
    // Redis key 기반으로 리스트 조회
    boolean containToken(String value);
    // Redis key 기반으로 BlackList를 조회
    Set<Object> getTokenBlackList();
    // Redis key 기반으로 리스트 내 요소 제거
    void removeToken(String value);
}
