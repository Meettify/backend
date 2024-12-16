package com.example.meettify.controller.admin;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.ResponseAnswerCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.dto.member.ResponseMemberDTO;
import com.example.meettify.dto.question.ReplyStatus;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.service.admin.AdminService;
import com.example.meettify.service.answer.AnswerCommentService;
import com.example.meettify.service.member.MemberService;
import com.example.meettify.service.notification.NotificationService;
import com.example.meettify.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
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
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminControllerDocs {
    private final MemberService memberService;
    private final QuestionService questionService;
    private final AnswerCommentService answerCommentService;
    private final NotificationService notificationService;
    private final AdminService adminService;

    // 모든 회원 정보 가져오기
    @Override
    @GetMapping("/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllMembers(Pageable page, @RequestParam String memberEmail) {
        try {
            Page<ResponseMemberDTO> members = memberService.getMembers(page, memberEmail);
            Map<String, Object> response = responsePageInfo(members);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new MemberException(e.getMessage());
        }
    }

    private static @NotNull Map<String, Object> responsePageInfo(Page<?> info) {
        Map<String, Object> response = new HashMap<>();
        // 현재 페이지의 아이템 목록
        response.put("contents", info.getContent());
        // 현재 페이지 번호
        response.put("nowPageNumber", info.getNumber() + 1);
        // 전체 페이지 수
        response.put("totalPage", info.getTotalPages());
        // 한 페이지에 출력되는 데이터 개수
        response.put("pageSize", info.getSize());
        // 다음 페이지 존재 여부
        response.put("hasNextPage", info.hasNext());
        // 이전 페이지 존재 여부
        response.put("hasPreviousPage", info.hasPrevious());
        // 첫 번째 페이지 여부
        response.put("isFirstPage", info.isFirst());
        // 마지막 페이지 여부
        response.put("isLastPage", info.isLast());
        return response;
    }

    // 모든 문의글 보기
    @Override
    @GetMapping("/questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllQuestions(Pageable page,
                                             @RequestParam(required = false) ReplyStatus replyStatus) {
        try {
            Page<ResponseQuestionDTO> questions = questionService.getAllQuestions(page, replyStatus);
            Map<String, Object> response = responsePageInfo(questions);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 답변 달기
    @Override
    @PostMapping("/{questionId}/answer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId,
                                          @RequestBody CreateAnswerDTO answer,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername() != null ? userDetails.getUsername() : "";
            ResponseAnswerCommentDTO response = answerCommentService.createAnswerComment(questionId, answer, email);
            // 문의글에 답변 달린 알람 보내기
            notificationService.notifyMessage(response.getWriterEmail(), "문의글에 답변이 달렸습니다.");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new CommentException(e.getMessage());
        }
    }

    // 답변 수정
    @Override
    @PutMapping("/{questionId}/answer/{answerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateAnswer(@PathVariable Long answerId,
                                          @RequestBody UpdateCommentDTO answer) {
        try {
            ResponseAnswerCommentDTO response = answerCommentService.updateAnswerComment(answerId, answer);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new CommentException(e.getMessage());
        }
    }

    // 답변 삭제
    @Override
    @DeleteMapping("/{questionId}/answer/{answerId}" )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId) {
        try {
            String response = answerCommentService.deleteAnswerComment(answerId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new CommentException(e.getMessage());
        }
    }

    // 회원 탈퇴
    @Override
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long memberId) {
        try {
            String response = adminService.removeMember(memberId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new MemberException(e.getMessage());
        }
    }

    // 회원 수 카운트
    @Override
    @GetMapping("/users")
    public ResponseEntity<?> countMembers() {
        try {
            Long response = memberService.countMembers();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new MemberException(e.getMessage());
        }
    }
}
