package com.example.meettify.dto.meetBoard;


import com.example.meettify.dto.meet.MeetPermissionDTO;
import lombok.*;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시글 권한 관련 정보를 저장하기 위한 DTO
 *   date    : 2024/10/06
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access =  AccessLevel.PROTECTED)
@Builder
public class MeetBoardPermissionDTO {
    private boolean canEdit;
    private boolean canDelete;

    public static MeetBoardPermissionDTO of(boolean canEdit, boolean canDelete) {
        return MeetBoardPermissionDTO.builder()
                .canEdit(canEdit)
                .canDelete(canDelete)
                .build();
    }
}
