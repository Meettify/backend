package com.example.meettify.repository.jwt;

import com.example.meettify.entity.jwt.TokenEntity;
import org.springframework.data.repository.CrudRepository;

/*
 *   worker  : 유요한
 *   work    : 토큰을 레디스에 넣어줄 레포지토리
 *   date    : 2024/10/26
 * */
public interface TokenRepository extends CrudRepository<TokenEntity, Long> {
    TokenEntity findByEmail(String email);
}
