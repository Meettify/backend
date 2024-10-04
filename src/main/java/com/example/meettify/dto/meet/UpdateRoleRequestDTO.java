package com.example.meettify.dto.meet;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UpdateRoleRequestDTO {
    @NotNull(message = "Role 값은 필수입니다.")
    private MeetRole newRole;  // 이 필드와 일치하는 JSON 필드를 전달해야 합니다.
}