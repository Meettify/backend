package com.example.meettify.controller.admin;

import com.example.meettify.dto.member.ResponseMemberDTO;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    // 모든 회원 정보 가져오기
    @Override
    @GetMapping("/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllMembers(Pageable page, String memberEmail) {
        try {
            Page<ResponseMemberDTO> members = memberService.getMembers(page, memberEmail);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("contents", members.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", members.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", members.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", members.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", members.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", members.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", members.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", members.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new MemberException(e.getMessage());
        }
    }
}
