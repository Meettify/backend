package com.example.meettify.service.answer;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;

public interface AnswerCommentService {
    // 답변 댓글 생성
    ResponseCommentDTO createAnswerComment(Long questionId, CreateAnswerDTO answer, String email);
    // 답변 댓글 수정
    ResponseCommentDTO updateAnswerComment(Long answerId, UpdateCommentDTO answer);
}
