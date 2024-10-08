package com.example.meettify.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class UpdateMemberDTO {
    @Schema(description = "회원 닉네임")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "닉네임은 영어와 한글만 가능합니다.")
    private String nickName;

    @Schema(description = "회원 기존 비밀번호")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}",
            message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~20자의 비밀번호여야 합니다." )
    private String originalMemberPw;

    @Schema(description = "회원 수정 비밀번호")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}",
            message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~20자의 비밀번호여야 합니다." )
    private String updateMemberPw;

    @Schema(description = "회원 주소")
    private AddressDTO memberAddr;
}
