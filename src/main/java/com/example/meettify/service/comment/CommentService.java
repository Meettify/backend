package com.example.meettify.service.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;

public interface CommentService {
    // 댓글 생성
    ResponseCommentDTO createComment(Long communityId, CreateCommentDTO comment, String email );
    // 댓글 수정
    ResponseCommentDTO updateComment(Long communityId, Long commentId, UpdateCommentDTO comment, String email );
    // 댓글 삭제
    String deleteComment(Long commentId);

}
