package com.example.meettify.service.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;

public interface QuestionService {
    // 문의 등록
    ResponseQuestionDTO saveQuestion(CreateBoardDTO question, String email);
    // 문의 수정
    ResponseQuestionDTO updateQuestion(Long questionId,
                                       UpdateQuestionDTO question,
                                       String email);
    // 문의 삭제
    String deleteQuestion(Long questionId);
}
