package com.example.meettify.dto.meetBoard;



import com.example.meettify.entity.meetBoard.CommentEntity;
import lombok.*;

import java.time.LocalDateTime;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시판 댓글 상세 정보를 표현하기 위한 DTO
 *   date    : 2024/10/03
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
    private LocalDateTime postDate;

    public static ResponseMeetBoardCommentDTO changeDTO(CommentEntity commentEntity) {
        return ResponseMeetBoardCommentDTO.builder()
                .commentId(commentEntity.getCommentId())
                .parentComment(commentEntity.getParentComment() !=null ? commentEntity.getParentComment().getCommentId() : null)
                .commentNickName(commentEntity.getMemberEntity().getNickName())
                .content(commentEntity.getContent())
                .postDate(commentEntity.getPostDate())
                .build();
    }

}
