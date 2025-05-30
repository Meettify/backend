package com.example.meettify.config.oauth;

import com.example.meettify.config.security.PrincipalDetails;
import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.jpa.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/*
 *   worker : 유요한
 *   work : 소셜 로그인 성공시 정보를 가져와서 회원가입과 JWT를 발급해주는 클래스
 *   date : 2024/09/22
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalOAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest.getClientRegistration()은 인증 및 인가된 사용자 정보를 가져오는
        // 시큐리티에서 제공하는 메서드입니다.
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        // 소셜 로그인시 발급받는 토큰
        String socialToken = userRequest.getAccessToken().getTokenValue();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                new DefaultOAuth2UserService();

        // 소셜 로그인한 유저정보를 가져온다.
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        // 회원가입
        OAuthUserInfo oAuthUserInfo = null;
        String registrationId = clientRegistration.getRegistrationId();
        log.debug("registrationId: " + registrationId);

        if(registrationId.equals("google")) {
            log.debug("구글 로그인");
            oAuthUserInfo = new GoogleUser(oAuth2User, clientRegistration);
        } else if (registrationId.equals("naver")) {
            log.debug("네이버 로그인");
            oAuthUserInfo = new NaverUser(oAuth2User, clientRegistration);
        } else {
            log.error("지원하지 않는 소셜 로그인입니다.");
        }

        // 어느 소셜로그인이지 정보를 가지고 온다.
        String provider = oAuthUserInfo.getProvider();
        // 소셜 서비스(provider)에서 발급된 고유 식별자를 가져온다.
        // 이 값은 소셜 서비스에서 유니크한 사용자를 식별하는 용도로 사용
        String providerId = oAuthUserInfo.getProviderId();
        String name = oAuthUserInfo.getName();
        // 사용자의 이메일을 가져온다.
        String email = oAuthUserInfo.getEmail();
        // 소셜 로그인의 경우 무조건 USER 등급으로 고정이다.
        UserRole role = UserRole.USER;

        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        boolean isMember = memberRepository.existsByMemberEmail(email);

        if(!isMember) {
            log.debug("소셜로그인 회원가입을 진행합니다.");

            findMember = MemberEntity.builder()
                    .memberEmail(email)
                    .memberName(name)
                    .provider(provider)
                    .providerId(providerId)
                    .nickName(name)
                    .memberRole(role)
                    .build();
            log.debug("findMember ={}", findMember);
            findMember = memberRepository.save(findMember);
        }

        if(isMember) {
            log.debug("로그인을 이미 한적이 있습니다.");
        }


        // attributes가 있는 생성자를 사용하여 PrincipalDetails 객체 생성
        // 소셜 로그인인 경우에는 attributes도 함께 가지고 있는 PrincipalDetails 객체를 생성하게 됩니다.
        PrincipalDetails principalDetails = new PrincipalDetails(findMember, oAuth2User.getAttributes());
        log.debug("principalDetails : " + principalDetails);
        return principalDetails;
    }
}
