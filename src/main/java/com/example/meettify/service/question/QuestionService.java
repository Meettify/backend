package com.example.meettify.service.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;

public interface QuestionService {
    // 문의 등록
    ResponseQuestionDTO saveQuestion(CreateBoardDTO question, String email);
}
