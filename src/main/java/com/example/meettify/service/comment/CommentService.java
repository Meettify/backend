package com.example.meettify.service.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;

public interface CommentService {
    ResponseCommentDTO createComment(Long communityId, CreateCommentDTO comment, String email );
}
