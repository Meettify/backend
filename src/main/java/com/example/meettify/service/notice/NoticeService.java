package com.example.meettify.service.notice;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    // 공지 생성
    ResponseBoardDTO saveBoard(CreateServiceDTO notice, String adminEmail);
    // 공지 수정
    ResponseBoardDTO updateBoard(Long noticeId, UpdateServiceDTO notice);
    // 공지 조회
    ResponseBoardDTO getNotice(Long noticeId);
    // 공지 삭제
    String deleteNotice(Long noticeId);
    // 공지 페이징
    Page<ResponseBoardDTO> getAllNotice(Pageable pageable);
}
