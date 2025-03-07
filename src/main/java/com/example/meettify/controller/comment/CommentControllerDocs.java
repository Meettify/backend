package com.example.meettify.controller.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "댓글", description = "댓글 API")
public interface CommentControllerDocs {
    @Operation(summary = "댓글 생성", description = "댓글 생성하는 API")
    ResponseEntity<?> createComment(Long communityId, CreateCommentDTO comment, UserDetails userDetails);

    @Operation(summary = "댓글 수정", description = "댓글 수정하는 API")
    ResponseEntity<?> updateComment(Long commentId, Long communityId, UpdateCommentDTO comment, UserDetails userDetails);

    @Operation(summary = "댓글 삭제", description = "댓글 삭제하는 API")
    ResponseEntity<?> deleteComment(Long commentId);

    @Operation(summary = "댓글 상세페이지", description = "댓글 조회 API")
    ResponseEntity<?> getComment(Long communityId);

    @Operation(summary = "댓글 페이징 조회", description = "댓글 페이징 조회 API")
    ResponseEntity<?> getComments(Long communityId, Pageable pageable);
}
