package com.example.meettify.controller.community;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateBoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "community", description = "커뮤니티 API")
public interface CommunityControllerDocs {
    @Operation(summary = "커뮤니티 등록", description = "커뮤니티 글을 생성하는 API")
    ResponseEntity<?> createCommunity(CreateBoardDTO community,
                                      List<MultipartFile> files,
                                      UserDetails userDetails);

    @Operation(summary = "커뮤니티 수정", description = "커뮤니티 글을 수정하는 API")
    ResponseEntity<?> updateCommunity(Long communityId,
                                      UpdateBoardDTO community,
                                      List<MultipartFile> files,
                                      UserDetails userDetails);

    @Operation(summary = "커뮤니티 조회", description = "커뮤니티 글을 조회하는 API")
    ResponseEntity<?> communityDetail(Long communityId);

    @Operation(summary = "커뮤니티 삭제", description = "커뮤니티 글을 삭제하는 API")
    ResponseEntity<?> deleteCommunity(Long communityId);

    @Operation(summary = "커뮤니티 페이징", description = "커뮤니티 글을 페이징 처리해서 보여줄 API")
    ResponseEntity<?> communityList(Pageable pageable);

    @Operation(summary = "커뮤니티 제목 검색", description = "커뮤니티 제목 검색 페이징 API")
    ResponseEntity<?> searchCommunity(Pageable pageable, String searchTitle);
}
