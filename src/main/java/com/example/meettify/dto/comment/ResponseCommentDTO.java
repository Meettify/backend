package com.example.meettify.dto.comment;

import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.comment.CommentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Schema(description = "작성자 유저 번호")
    private Long memberId;
    @Schema(description = "댓글 삭제 표시")
    private boolean isDeleted = false;


    @Builder.Default
    private List<ResponseCommentDTO> children = new ArrayList<>();  // 자식 댓글 리스트

    // 댓글 생성할 때 반환
    public static ResponseCommentDTO changeDTO(CommentEntity comment,
                                               String nickName,
                                               Long parentCommentId) {
        // 기본적으로 children 리스트를 사용
        List<ResponseCommentDTO> childrenDTO = comment.getChildren().stream()
                .map(child -> childChangeDTO(
                        child,
                        child.getMember().getNickName(),
                        child.getParent() != null ? child.getParent().getCommentId() : 0L))
                .collect(Collectors.toList());

        return ResponseCommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .createdAt(comment.getRegTime())
                .nickName(nickName)
                .parentCommentId(parentCommentId)
                .children(childrenDTO)
                .memberId(comment.getMember().getMemberId())
                .build();
    }

    // DTO로 변환
    public static ResponseCommentDTO changeDTO(CommentEntity comment,
                                               String nickName,
                                               Long parentCommentId,
                                               List<CommentEntity> findCommentOfChild) {

        // 자식 댓글 DTO 처리
        List<ResponseCommentDTO> childrenDTO = comment.getChildren().stream()
                .map(child -> childChangeDTO(
                        child,
                        child.getMember().getNickName(),
                        child.getParent() != null ? child.getParent().getCommentId() : 0L))
                .collect(Collectors.toList());

        // 자식 댓글의 댓글들 조회
        List<ResponseCommentDTO> commentOfChild = findCommentOfChild.stream()
                .map(child -> childChangeDTO(
                        child,
                        child.getMember().getNickName(),
                        child.getParent() != null ? child.getParent().getCommentId() : 0L))
                .toList();

        // parentCommentId를 key로 하는 Map 생성
        // 예: {100L: [대댓글1, 대댓글2], 101L: [대댓글3]}
        Map<Long, List<ResponseCommentDTO>> childMap = commentOfChild.stream()
                .collect(Collectors.groupingBy(ResponseCommentDTO::getParentCommentId));


        // 자식 댓글들에 댓글이 있는지 보고 있으면 넣어줌
        for (ResponseCommentDTO child : childrenDTO) {
            // child.getCommentId()가 곧 Map의 Key
            // 기존에 stream으로 filter을 거는 것을 이거로 접근 조건을 바꿔서 처리
            List<ResponseCommentDTO> childrenComment = childMap.getOrDefault(child.getCommentId(), List.of());
            // 자식 댓글에 해당 자식 댓글들을 넣어줌
            child.children.addAll(childrenComment);
        }

        return ResponseCommentDTO.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .createdAt(comment.getRegTime())
                .nickName(nickName)
                .parentCommentId(parentCommentId)
                .children(childrenDTO)
                .memberId(comment.getMember().getMemberId())
                .isDeleted(comment.isDeleted())
                .build();
    }

    // 자식 댓글 처리
    public static ResponseCommentDTO childChangeDTO(CommentEntity children,
                                                    String nickName,
                                                    Long parentCommentId) {
        return ResponseCommentDTO.builder()
                .commentId(children.getCommentId())
                .comment(children.getComment())
                .createdAt(children.getRegTime())
                .nickName(nickName)
                .parentCommentId(parentCommentId)
                .memberId(children.getMember().getMemberId())
                .isDeleted(children.isDeleted())
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
        return children != null ? this.commentId : null;
    }
}
