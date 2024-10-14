package com.example.meettify.service.notice;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;

public interface NoticeService {
    // 공지 생성
    ResponseBoardDTO saveBoard(CreateServiceDTO notice, String adminEmail);
    // 공지 수정
    ResponseBoardDTO updateBoard(Long noticeId, UpdateServiceDTO notice);
    // 공지 조회
    ResponseBoardDTO getNotice(Long noticeId);
}
