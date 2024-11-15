package com.example.meettify.controller.admin;

import com.example.meettify.dto.member.ResponseMemberDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.service.member.MemberService;
import com.example.meettify.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminControllerDocs{
    private final MemberService memberService;
    private final QuestionService questionService;

    // 모든 회원 정보 가져오기
    @Override
    @GetMapping("/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllMembers(Pageable page, String memberEmail) {
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

    @Override
    @GetMapping("/questions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllQuestions(Pageable page) {
        try {
            Page<ResponseQuestionDTO> questions = questionService.getAllQuestions(page);
            Map<String, Object> response = responsePageInfo(questions);

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }
}
