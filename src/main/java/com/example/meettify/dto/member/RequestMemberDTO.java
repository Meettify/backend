package com.example.meettify.dto.member;

import com.example.meettify.dto.member.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestMemberDTO {
    @Schema(description = "이메일", example = "test@test.com")
    @NotNull(message = "이메일은 필수입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 아닙니다.")
    @Email(message = "이메일 형식에 맞춰주세요")
    private String memberEmail;
    @Schema(description = "회원 이름")
    @NotNull(message = "이름은 필수입니다.")
    private String memberName;

    @Schema(description = "회원 닉네임")
    @NotNull(message = "닉네임은 필수입니다.")
    private String nickName;

    @Schema(description = "회원 비밀번호")
    private String memberPw;

    @Schema(description = "회원 권한")
    @NotNull(message = "권한은 필수입니다.")
    private UserRole memberRole;

    @Schema(description = "회원 주소")
    @NotNull(message = "주소는 필수입니다.")
    private AddressDTO memberAddr;
}
