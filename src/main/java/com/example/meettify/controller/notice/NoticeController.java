package com.example.meettify.controller.notice;

import com.example.meettify.dto.board.*;
import com.example.meettify.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/notice")
public class NoticeController implements NoticeControllerDocs {
    private final NoticeService noticeService;
    private final ModelMapper modelMapper;

    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> saveNotice(@RequestBody CreateBoardDTO notice,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CreateServiceDTO changeServiceNotice = modelMapper.map(notice, CreateServiceDTO.class);
            log.info("service DTO: {}", changeServiceNotice);
            ResponseBoardDTO response = noticeService.saveBoard(changeServiceNotice, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 공지 수정
    @Override
    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(@PathVariable Long noticeId,
                                          @RequestBody UpdateBoardDTO notice,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            UpdateServiceDTO changeServiceNotice = modelMapper.map(notice, UpdateServiceDTO.class);
            log.info("service DTO: {}", changeServiceNotice);
            ResponseBoardDTO response = noticeService.updateBoard(noticeId, changeServiceNotice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
