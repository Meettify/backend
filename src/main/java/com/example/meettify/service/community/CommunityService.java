package com.example.meettify.service.community;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    // 문의 등록
    ResponseBoardDTO saveBoard(CreateServiceDTO board,
                               List<MultipartFile> files,
                               String memberEmail);
}
