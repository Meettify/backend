package com.example.meettify.dto.jwt;

import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.jwt.TokenEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
/*
*   writer  : 유요한
*   work    : 로그인 시 프론트에게 JWT를 발급하기 위한 용도
*   date    : 2024/09/27
* */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class TokenDTO {
    @Schema(description = "토큰 타입")
    private String grantType;
    @Schema(description = "accessToken")
    private String accessToken;
    @Schema(description = "refreshToken")
    private String refreshToken;
    @Schema(description = "유저 이메일")
    private String memberEmail;
    @Schema(description = "유저 번호")
    private Long memberId;
    @Schema(description = "유저 권한")
    private UserRole memberRole;
    @Schema(description = "유저 닉네임")
    private String nickName;


    public static TokenDTO changeDTO(TokenEntity token,
                                     String accessToken,
                                     UserRole memberRole,
                                     String nickName) {
        return TokenDTO.builder()
                .grantType(token.getGrantType())
                .accessToken(accessToken)
                .refreshToken(token.getRefreshToken())
                .memberEmail(token.getEmail())
                .memberId(token.getMemberId())
                .memberRole(memberRole)
                .nickName(nickName)
                .build();
    }
}
