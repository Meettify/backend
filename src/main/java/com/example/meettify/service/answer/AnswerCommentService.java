package com.example.meettify.service.answer;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;

public interface AnswerCommentService {
    // 답변 댓글 생성
    ResponseCommentDTO createAnswerComment(Long questionId, CreateAnswerDTO answer, String email);
}
