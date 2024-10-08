package com.example.meettify.controller.community;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateBoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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
    ResponseEntity<?> itemDetail(Long communityId);
}
