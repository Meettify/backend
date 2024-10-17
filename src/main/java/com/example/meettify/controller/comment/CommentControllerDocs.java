package com.example.meettify.controller.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "댓글", description = "댓글 API")
public interface CommentControllerDocs {
    @Operation(summary = "댓글 생성", description = "댓글 생성하는 API")
    ResponseEntity<?> createComment(Long communityId, CreateCommentDTO comment, UserDetails userDetails);
}
