package com.example.meettify.service.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface QuestionService {
    // 문의 등록
    ResponseQuestionDTO saveQuestion(CreateBoardDTO question, String email);
    // 문의 수정
    ResponseQuestionDTO updateQuestion(Long questionId,
                                       UpdateQuestionDTO question,
                                       String email);
    // 문의 삭제
    String deleteQuestion(Long questionId);
    // 문의 조회
    ResponseQuestionDTO getQuestion(Long questionId, UserDetails userDetails);
    // 내 문의글 보기
    Page<ResponseQuestionDTO> getMyAllQuestions(Pageable pageable, String memberEmail);
    // 모든 문의글 보기 - 관리자
    Page<ResponseQuestionDTO> getAllQuestions(Pageable pageable);
}
