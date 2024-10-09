package com.example.meettify.service.community;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    // 커뮤니티 생성
    ResponseBoardDTO saveBoard(CreateServiceDTO board,
                               List<MultipartFile> files,
                               String memberEmail);

    // 커뮤니티 수정
    ResponseBoardDTO updateBoard(Long communityId,
                                 UpdateServiceDTO board,
                                 List<MultipartFile> files);
}
