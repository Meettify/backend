package com.example.meettify.controller.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
