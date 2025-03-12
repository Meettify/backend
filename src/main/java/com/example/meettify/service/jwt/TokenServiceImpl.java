package com.example.meettify.service.jwt;

import com.example.meettify.config.jwt.JwtProvider;
import com.example.meettify.dto.jwt.TokenDTO;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.jwt.TokenEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.redis.jwt.TokenRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    // accessToken이 만료시 재발급해주는 로직
    @Override
    public TokenDTO reissuanceAccessToken(String email, String refreshToken) {
        try {
            // 레디스에서 토큰 조회
            TokenEntity findToken = tokenRepository.findByEmail(email);
            if (findToken == null) {
                log.warn("Token not found for email: {}", email);
                throw new JwtException("No token found for email: " + email);
            }
            log.debug("토큰 소유주 체크 : {}", findToken.getEmail());
            log.debug("리프레쉬 토큰 {}", refreshToken);

            // 리프레시 토큰 검증
            if (!findToken.getRefreshToken().equals(refreshToken)) {
                throw new JwtException("유효하지 않은 리프레시 토큰입니다.");
            }
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 권한 설정
            List<GrantedAuthority> authorities = getAuthorities(findMember);
            // 토큰 재발급
            TokenDTO token = jwtProvider.createToken(email, authorities, findMember.getMemberId());
            log.debug("token: {}", token);
            // 레디스에 토큰 저장
            tokenRepository.save(TokenEntity.changeEntity(token));
            return token;
        } catch (Exception e) {
            throw new JwtException("토큰 발급하는데 실패했습니다.");
        }
    }

    // 기존의 유저의 권한을 추출
    List<GrantedAuthority> getAuthorities(MemberEntity member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        UserRole memberRole = member.getMemberRole();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        return authorities;
    }
}
