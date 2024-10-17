package com.example.meettify.dto.comment;

import com.example.meettify.entity.comment.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/*
 *   worker  : 유요한
 *   work    : 프론트에게 반환할 댓글 클래스
 *   date    : 2024/10/17
 * */
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class ResponseCommentDTO {
    @Schema(description = "댓글 번호")
    private Long commentId;
    @Schema(description = "댓글")
    private String comment;
    @Schema(description = "답변 등록 시간")
    private LocalDateTime createdAt;

    // DTO로 변환
    public static ResponseCommentDTO changeDTO(CommentEntity comment) {
        return ResponseCommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .createdAt(comment.getRegTime())
                .build();
    }
}
