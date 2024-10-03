package com.example.meettify.dto.meet;


/*
 *   worker  : 조영흔
 *   work    : 모임 관리자, 멤버, 대기자, 제명자(강퇴당한 사람)
 *   date    : 2024/09/19
 * */
public enum MeetRole {

    // 관리자, 멤버, 대기자, 비회원, 추방자
    ADMIN,MEMBER,WAITING,OUTSIDER,EXPEL,
}
