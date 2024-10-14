package com.example.meettify.service.community;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 커뮤니티 상세 페이지
    ResponseBoardDTO getBoard(Long communityId);

    // 커뮤니티 삭제
    String deleteBoard(Long communityId);

    // 커뮤니티 전체 조회
    Page<ResponseBoardDTO> getBoards(Pageable pageable);

    // 커뮤니티 제목 검색
    Page<ResponseBoardDTO> searchTitle(Pageable pageable, String searchTitle);
}
