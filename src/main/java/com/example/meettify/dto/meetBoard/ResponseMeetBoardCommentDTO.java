package com.example.meettify.dto.meetBoard;



import com.example.meettify.entity.meetBoard.MeetBoardCommentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 *   worker  : 조영흔, 유요한
 *   work    : 모임 게시판 댓글 상세 정보를 표현하기 위한 DTO
 *   date    : 2025/05/09
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseMeetBoardCommentDTO {
    private Long commentId;
    private Long parentComment;
    private String commentNickName;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime postDate;
    private MeetBoardCommentPermissionDTO permissionDTO;
    @Builder.Default
    private List<ResponseMeetBoardCommentDTO> children = new ArrayList<>();

    public static ResponseMeetBoardCommentDTO changeDTO(MeetBoardCommentEntity meetBoardCommentEntity,
                                                        MeetBoardCommentPermissionDTO permissionDTO) {
        return ResponseMeetBoardCommentDTO.builder()
                .commentId(meetBoardCommentEntity.getCommentId())
                .parentComment(meetBoardCommentEntity.getParentComment() != null ? meetBoardCommentEntity.getParentComment().getCommentId() : null)
                .commentNickName(meetBoardCommentEntity.getMemberEntity().getNickName())
                .content(meetBoardCommentEntity.getContent())
                .postDate(meetBoardCommentEntity.getPostDate())
                .permissionDTO(permissionDTO)
                .build();
    }
}
