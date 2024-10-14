package com.example.meettify.controller.community;

import com.example.meettify.dto.board.*;
import com.example.meettify.service.community.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController implements CommunityControllerDocs {
    private final CommunityService communityService;
    private final ModelMapper modelMapper;

    // 커뮤니티 생성
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCommunity(@RequestPart CreateBoardDTO community,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CreateServiceDTO changeServiceDTO = modelMapper.map(community, CreateServiceDTO.class);
            log.info("service DTO: {}", changeServiceDTO);
            ResponseBoardDTO response = communityService.saveBoard(changeServiceDTO, files, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 커뮤니티 수정
    @Override
    @PutMapping("/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCommunity(@PathVariable Long communityId,
                                             @RequestPart UpdateBoardDTO community,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("기능 사용중인 email" + email);
            UpdateServiceDTO updateServiceDTO = modelMapper.map(community, UpdateServiceDTO.class);
            ResponseBoardDTO response = communityService.updateBoard(communityId, updateServiceDTO, files);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 커뮤니티 조회
    @Override
    @GetMapping("/{communityId}")
    public ResponseEntity<?> itemDetail(@PathVariable Long communityId) {
        try {
            ResponseBoardDTO response = communityService.getBoard(communityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{communityId}")
    public ResponseEntity<?> deleteCommunity(Long communityId) {
        try {
            String response = communityService.deleteBoard(communityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
