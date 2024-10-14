package com.example.meettify.controller.notice;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateBoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "notice", description = "공지 API")
public interface NoticeControllerDocs {
    @Operation(summary = "공지사항 등록", description = "공지사항을 등록하는 API")
    ResponseEntity<?> saveNotice(CreateBoardDTO notice, UserDetails userDetails);

    @Operation(summary = "공지사항 수정", description = "공지사항 수정하는 API")
    ResponseEntity<?> updateNotice(Long noticeId, UpdateBoardDTO notice, UserDetails userDetails);

    @Operation(summary = "공지사항 상세페이지", description = "공지사항 상세페이지 API")
    ResponseEntity<?> noticeDetail(Long noticeId);
}
