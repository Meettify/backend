package com.example.meettify.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class RequestAccessEmailDTO {
    @Schema(description = "채팅방에 입장시킬 유저 이메일")
    private String accessEmail;

}
