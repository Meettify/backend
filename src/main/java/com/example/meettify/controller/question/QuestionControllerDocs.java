package com.example.meettify.controller.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "문의하기", description = "문의하기 API")
public interface QuestionControllerDocs {
    @Operation(summary = "문의하기", description = "문의하는 API")
    ResponseEntity<?> saveQuestion(CreateBoardDTO question, UserDetails userDetails);

    @Operation(summary = "문의 수정", description = "문의 수정 API")
    ResponseEntity<?> updateQuestion(Long questionId, UpdateQuestionDTO question, UserDetails userDetails);

    @Operation(summary = "문의 삭제", description = "문의 삭제 API")
    ResponseEntity<?> deleteQuestion(Long questionId, UserDetails userDetails);

    @Operation(summary = "문의 조회", description = "문의 조회 API")
    ResponseEntity<?> getQuestions(Long questionId, UserDetails userDetails);

    @Operation(summary = "내 문의 조회", description = "내 문의 조회 API")
    ResponseEntity<?> getMyQuestions(Pageable pageable, UserDetails userDetails);

    @Operation(summary = "내 문의글 수", description = "내 문의글 수량 체크 API")
    ResponseEntity<?> countMyQuestions(UserDetails userDetails);

    @Operation(summary = "모든 문의글 수", description = "모든 문의글 수량 체크 API")
    ResponseEntity<?> countQuestions();
}
