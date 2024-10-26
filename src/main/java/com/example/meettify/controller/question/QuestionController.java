package com.example.meettify.controller.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/questions")
public class QuestionController implements QuestionControllerDocs{
    private final QuestionService questionService;

    // 문의글 등록
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> saveQuestion(@RequestBody CreateBoardDTO question,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseQuestionDTO response = questionService.saveQuestion(question, email);
            log.info(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 문의글 수정
    @Override
    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId,
                                            @RequestBody UpdateQuestionDTO question,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseQuestionDTO response = questionService.updateQuestion(questionId, question, email);
            log.info(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 문의글 삭제
    @Override
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info(email);
            String response = questionService.deleteQuestion(questionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 문의글 조회
    @Override
    @GetMapping("/{questionId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getQuestions(@PathVariable Long questionId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ResponseQuestionDTO response = questionService.getQuestion(questionId, userDetails);
            log.info(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }
}
