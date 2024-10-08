package com.example.meettify.dto.meet;


import io.swagger.v3.oas.annotations.media.Schema;

/*
 *   worker  : 조영흔
 *   work    : 모임 관리자, 멤버, 대기자, 비회원, 제명자(강퇴당한 사람)
 *   date    : 2024/09/19
 * */
public enum MeetRole {

    @Schema(name = "관리자")
    ADMIN,
    @Schema(name = "멤버")
    MEMBER,
    @Schema(name = "대기 멤버")
    WAITING,
    @Schema(name = "비회원")
    OUTSIDER,
    @Schema(name = "추방자")
    EXPEL,
}
