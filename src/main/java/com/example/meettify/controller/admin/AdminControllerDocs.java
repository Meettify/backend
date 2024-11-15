package com.example.meettify.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "관리자 컨트롤러", description = "관리자 기능")
public interface AdminControllerDocs {
    @Operation(summary = "회원 리스트", description = "전체적인 회원들을 가져오는 API")
    ResponseEntity<?> getAllMembers(Pageable page, String memberEmail);

    @Operation(summary = "문의글 내역 보기", description = "전체적인 문의글 내역 가져오는 API")
    ResponseEntity<?> getAllQuestions(Pageable page);
}
