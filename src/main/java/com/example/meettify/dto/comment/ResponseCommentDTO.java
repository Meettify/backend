package com.example.meettify.dto.comment;

import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.comment.CommentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @Schema(description = "작성 닉네임")
    private String nickName;
    @Schema(description = "부모 번호")
    private Long parentCommentId;

    @Builder.Default
    private List<ResponseCommentDTO> children = new ArrayList<>();  // 자식 댓글 리스트

    // DTO로 변환
    public static ResponseCommentDTO changeDTO(CommentEntity comment, String nickName, Long parentCommentId) {
        return ResponseCommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .createdAt(comment.getRegTime())
                .nickName(nickName)
                .parentCommentId(parentCommentId)
                .build();
    }

    // 어드민이 문의글 답변
    public static ResponseCommentDTO changeDTO(AnswerCommentEntity answer, String nickName) {
        return ResponseCommentDTO.builder()
                .commentId(answer.getAnswerId())
                .comment(answer.getAnswer())
                .createdAt(answer.getRegTime())
                .nickName(nickName)
                .parentCommentId(answer.getAnswerId())
                .build();
    }

    // 부모 댓글 ID 반환
    public Long getParentId() {
        return children != null  ? this.commentId : null;
    }
}
