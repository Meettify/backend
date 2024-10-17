package com.example.meettify.service.notice;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseNoticeDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    // 공지 생성
    ResponseNoticeDTO saveBoard(CreateServiceDTO notice, String adminEmail);
    // 공지 수정
    ResponseNoticeDTO updateBoard(Long noticeId, UpdateServiceDTO notice);
    // 공지 조회
    ResponseNoticeDTO getNotice(Long noticeId);
    // 공지 삭제
    String deleteNotice(Long noticeId);
    // 공지 페이징
    Page<ResponseNoticeDTO> getAllNotice(Pageable pageable);
}
