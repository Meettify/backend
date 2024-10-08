package com.example.meettify.dto.member.role;

import io.swagger.v3.oas.annotations.media.Schema;

public enum UserRole {
    @Schema(name = "일반 회원")
    USER,
    @Schema(name = "관리자")
    ADMIN
}
