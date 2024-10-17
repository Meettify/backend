package com.example.meettify.controller.community;

import com.example.meettify.dto.board.*;
import com.example.meettify.service.community.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController implements CommunityControllerDocs {
    private final CommunityService communityService;
    private final ModelMapper modelMapper;

    // 커뮤니티 생성
    @Override
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCommunity(@RequestPart CreateBoardDTO community,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CreateServiceDTO changeServiceDTO = modelMapper.map(community, CreateServiceDTO.class);
            log.info("service DTO: {}", changeServiceDTO);
            ResponseCommunityDTO response = communityService.saveBoard(changeServiceDTO, files, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 커뮤니티 수정
    @Override
    @PutMapping(value = "/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCommunity(@PathVariable Long communityId,
                                             @RequestPart("community") UpdateBoardDTO community,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("기능 사용중인 email" + email);
            UpdateServiceDTO updateServiceDTO = modelMapper.map(community, UpdateServiceDTO.class);
            ResponseCommunityDTO response = communityService.updateBoard(communityId, updateServiceDTO, files);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 커뮤니티 조회
    @Override
    @GetMapping("/{communityId}")
    public ResponseEntity<?> communityDetail(@PathVariable Long communityId) {
        try {
            ResponseCommunityDTO response = communityService.getBoard(communityId);
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

    @Override
    @GetMapping("/communityList")
    public ResponseEntity<?> communityList(Pageable pageable) {
        try {
            // 검색하지 않을 때는 모든 글을 보여준다.
            Page<ResponseCommunityDTO> community = communityService.getBoards(pageable);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("communities", community.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", community.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", community.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", community.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", community.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", community.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", community.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", community.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<?> searchCommunity(Pageable pageable,
                                             @RequestParam(value = "searchTitle") String searchTitle) {
        try {
            // 검색하지 않을 때는 모든 글을 보여준다.
            Page<ResponseCommunityDTO> community = communityService.searchTitle(pageable, searchTitle);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("communities", community.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", community.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", community.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", community.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", community.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", community.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", community.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", community.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
