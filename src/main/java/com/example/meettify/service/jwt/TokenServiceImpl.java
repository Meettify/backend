package com.example.meettify.service.jwt;

import com.example.meettify.config.jwt.JwtProvider;
import com.example.meettify.dto.jwt.TokenDTO;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.jwt.TokenEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.jwt.TokenRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    // accessToken이 만료시 재발급해주는 로직
    @Override
    public TokenDTO reissuanceAccessToken(String email) {
        try {
            TokenEntity findToken = tokenRepository.findByEmail(email);
            log.info("토큰 소유주 체크 : " + findToken.getEmail());

            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember.equals(findToken.getEmail())) {
                List<GrantedAuthority> authorities = getAuthorities(findMember);
                TokenDTO token = jwtProvider.getAccessToken(
                        email, authorities, findMember.getMemberId(), findToken.getRefreshToken());
                return token;
            }
            throw new MemberException("유저 정보가 맞지 않습니다.");
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
