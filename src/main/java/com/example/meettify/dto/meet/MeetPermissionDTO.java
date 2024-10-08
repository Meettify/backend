package com.example.meettify.dto.meet;

import lombok.*;

/*
 *   worker  : 조영흔
 *   work    : 모임 권한 관련 정보를 저장하기 위한 DTO
 *   date    : 2024/10/06
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetPermissionDTO {
    private boolean canEdit;
    private boolean canDelete;

    public static MeetPermissionDTO of(boolean canEdit, boolean canDelete) {
        return MeetPermissionDTO.builder()
                .canEdit(canEdit)
                .canDelete(canDelete)
                .build();
    }
}
