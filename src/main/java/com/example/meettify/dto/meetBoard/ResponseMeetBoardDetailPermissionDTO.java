package com.example.meettify.dto.meetBoard;

import lombok.*;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시판과 권한 상세 정보를 표현하기 위한 DTO
 *   date    : 2024/10/06
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseMeetBoardDetailPermissionDTO {
    private MeetBoardPermissionDTO meetBoardPermissionDTO;
    private MeetBoardDetailsDTO meetBoardDetailsDTO;
}
