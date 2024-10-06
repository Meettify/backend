package com.example.meettify.dto.meetBoard;


import lombok.*;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시글 댓글 권한 관련 정보를 저장하기 위한 DTO
 *   date    : 2024/10/06
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access =  AccessLevel.PROTECTED)
@Builder
public class MeetBoardCommentPermissionDTO {
    private boolean canEdit;
    private boolean canDelete;

    public static MeetBoardCommentPermissionDTO of(boolean canEdit, boolean canDelete) {
        return MeetBoardCommentPermissionDTO.builder()
                .canEdit(canEdit)
                .canDelete(canDelete)
                .build();
    }
}
