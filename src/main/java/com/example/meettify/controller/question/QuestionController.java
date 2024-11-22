package com.example.meettify.controller.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ResponseCountDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    @GetMapping("/my-questions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getMyQuestions(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Page<ResponseQuestionDTO> findAllQuestions = questionService.getMyAllQuestions(pageable, email);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("contents",findAllQuestions.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber",  findAllQuestions.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", findAllQuestions.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", findAllQuestions.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", findAllQuestions.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", findAllQuestions.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", findAllQuestions.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", findAllQuestions.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 내 문의글 수
    @Override
    @GetMapping("/count-my-questions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> countMyQuestions(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseCountDTO response = questionService.countMyAllQuestions(email);
            return ResponseEntity.ok(response);
        }  catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 모든 문의글 수
    @Override
    @GetMapping("/count-questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> countQuestions() {
        try {
            ResponseCountDTO response = questionService.countAllQuestions();
            return ResponseEntity.ok(response);
        }  catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }
}
