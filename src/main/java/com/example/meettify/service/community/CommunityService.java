package com.example.meettify.service.community;

import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {
    // 커뮤니티 생성
    ResponseCommunityDTO saveBoard(CreateServiceDTO board,
                                   List<MultipartFile> files,
                                   String memberEmail);

    // 커뮤니티 수정
    ResponseCommunityDTO updateBoard(Long communityId,
                                     UpdateServiceDTO board,
                                     List<MultipartFile> files);

    // 커뮤니티 상세 페이지
    ResponseCommunityDTO getBoard(Long communityId,
                                  HttpServletRequest request,
                                  HttpServletResponse response);

    // 커뮤니티 삭제
    String deleteBoard(Long communityId);

    // 커뮤니티 전체 조회
    Page<ResponseCommunityDTO> getBoards(Pageable pageable);

    // 커뮤니티 제목 검색
    Page<ResponseCommunityDTO> searchTitle(Pageable pageable, String searchTitle);

    // 본인 커뮤니티 글 보기
    Page<ResponseCommunityDTO> getMyBoards(Pageable pageable, String memberEmail);

}
