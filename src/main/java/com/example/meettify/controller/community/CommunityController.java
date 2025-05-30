package com.example.meettify.controller.community;

import com.example.meettify.dto.board.*;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.community.CommunityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
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
            throw new BoardException(e.getMessage());
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
            throw new BoardException(e.getMessage());
        }
    }

    // 커뮤니티 조회
    @Override
    @GetMapping("/{communityId}")
    public ResponseEntity<?> communityDetail(@PathVariable Long communityId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        try {
            ResponseCommunityDTO responseCommunity = communityService.getBoard(communityId, request, response);
            return ResponseEntity.ok(responseCommunity);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 커뮤니티 삭제
    @Override
    @DeleteMapping("/{communityId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCommunity(@PathVariable Long communityId) {
        try {
            String response = communityService.deleteBoard(communityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
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
            throw new BoardException(e.getMessage());
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
            throw new BoardException(e.getMessage());
        }
    }

    @Override
    @GetMapping("/my-community")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getCommunities(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info(email);

            // 검색하지 않을 때는 모든 글을 보여준다.
            Page<ResponseCommunityDTO> community = communityService.getMyBoards(pageable, email);
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
            throw new BoardException(e.getMessage());
        }
    }

    // 내 문의글 수 카운트
    @Override
    @GetMapping("/count-my-community")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> countCommunity(@AuthenticationPrincipal UserDetails userDetails) {
       try {
           String email = userDetails.getUsername();
           long responseCount = communityService.countMyCommunity(email);
           return ResponseEntity.ok(responseCount);
       } catch (Exception e) {
           throw new BoardException(e.getMessage());
       }
    }

    // 모든 문의글 수 카운트
    @Override
    @GetMapping("/count-community")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> countAllItems() {
        try {
            long responseCount = communityService.countAllCommunity();
            return ResponseEntity.ok(responseCount);
        } catch (Exception e) {
            throw new BoardException(e.getMessage());
        }
    }

    // 조회수 TOP10 가져오기
    @Override
    @GetMapping("/top")
    public ResponseEntity<?> getTopCommunities() {
        List<ResponseCommunityDTO> result = communityService.getTopBoards();
        log.debug("반환할 값 {}", result);
        return ResponseEntity.ok(result);
    }
}
