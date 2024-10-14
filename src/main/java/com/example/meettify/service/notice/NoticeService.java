package com.example.meettify.service.notice;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;

public interface NoticeService {
    // 공지 생성
    ResponseBoardDTO saveBoard(CreateServiceDTO notice, String adminEmail);
}
