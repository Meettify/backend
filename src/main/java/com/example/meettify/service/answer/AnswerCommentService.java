package com.example.meettify.service.answer;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.ResponseAnswerCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;

import java.util.List;

public interface AnswerCommentService {
    // 답변 댓글 생성
    ResponseAnswerCommentDTO createAnswerComment(Long questionId, CreateAnswerDTO answer, String email);
    // 답변 댓글 수정
    ResponseAnswerCommentDTO updateAnswerComment(Long answerId, UpdateCommentDTO answer);
    // 답변 댓글 삭제
    String deleteAnswerComment(Long answerId);
}
