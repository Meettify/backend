package com.example.meettify.controller.notice;

import com.example.meettify.dto.board.*;
import com.example.meettify.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/notice")
public class NoticeController implements NoticeControllerDocs {
    private final NoticeService noticeService;
    private final ModelMapper modelMapper;

    // 공지 등록
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveNotice(@RequestBody CreateBoardDTO notice,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CreateServiceDTO changeServiceNotice = modelMapper.map(notice, CreateServiceDTO.class);
            log.info("service DTO: {}", changeServiceNotice);
            ResponseNoticeDTO response = noticeService.saveBoard(changeServiceNotice, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 공지 수정
    @Override
    @PutMapping("/{noticeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId,
                                          @RequestBody UpdateBoardDTO notice,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            UpdateServiceDTO changeServiceNotice = modelMapper.map(notice, UpdateServiceDTO.class);
            log.info("service DTO: {}", changeServiceNotice);
            ResponseNoticeDTO response = noticeService.updateBoard(noticeId, changeServiceNotice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 공지사항 상세페이지
    @Override
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> noticeDetail(@PathVariable Long noticeId) {
        try {
            ResponseNoticeDTO response = noticeService.getNotice(noticeId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 공지사항 삭제
    @Override
    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId) {
        try {
            String response = noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 공지사항 페이징
    @Override
    @GetMapping("/noticeList")
    public ResponseEntity<?> noticeList(@PageableDefault(sort = "noticeId", direction = Sort.Direction.DESC)Pageable pageable) {
        try {
            Page<ResponseNoticeDTO> notice = noticeService.getAllNotice(pageable);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("communities",notice.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber",  notice.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", notice.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", notice.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", notice.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", notice.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", notice.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", notice.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
