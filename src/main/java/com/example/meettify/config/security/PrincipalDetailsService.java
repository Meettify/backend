package com.example.meettify.config.security;

import com.example.meettify.config.login.LoginAttemptConfig;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 *   worker : 유요한
 *   work : 이 클래스의 기능은 로그인시 회원을 조회하고 찾아서 PrincipalDetails에 넘겨준다.
 *   date : 2024/09/20
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final LoginAttemptConfig loginAttemptConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity findMember = memberRepository.findByMemberEmail(username);
        log.debug("member : " + findMember);

        if(loginAttemptConfig.isBlocked(username)) {
            throw new LockedException("Member is blocked for 1 day");
        }

        if(findMember == null) {
            throw new UsernameNotFoundException("해당 사용자가 없습니다.");
        } else {
            return new PrincipalDetails(findMember);
        }
    }
}
