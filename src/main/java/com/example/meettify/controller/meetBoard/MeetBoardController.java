package com.example.meettify.controller.meetBoard;

import com.example.meettify.dto.meetBoard.*;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.service.meetBoard.MeetBoardService;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/v1/meetBoards")
@RequiredArgsConstructor
public class MeetBoardController implements MeetBoardControllerDocs{
    private final MeetBoardService meetBoardService;

    //모임의 모임 게시판 리스트 조회
    @Override
    @GetMapping("list/{meetId}")
    public ResponseEntity<?> getList(
            @PathVariable Long meetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Pageable 객체 생성
            Pageable pageable = PageRequest.of(page, size);
            // 서비스에서 페이징된 게시글 리스트를 조회
            Page<MeetBoardSummaryDTO> meetBoardPage = meetBoardService.getPagedList(meetId, pageable);

            // 페이징 정보와 함께 응답할 데이터 준비
            Map<String, Object> response = new HashMap<>();
            response.put("meetBoardPage", meetBoardPage.getContent()); // 게시글 리스트
            response.put("currentPage", meetBoardPage.getNumber()); // 현재 페이지 번호
            response.put("totalItems", meetBoardPage.getTotalElements()); // 전체 아이템 개수
            response.put("totalPages", meetBoardPage.getTotalPages()); // 전체 페이지 수
            response.put("hasPrevious", meetBoardPage.hasPrevious()); // 직전 페이지 존재 여부
            response.put("hasNext", meetBoardPage.hasNext()); // 다음 페이지 존재 여부
            response.put("isFirst", meetBoardPage.isFirst()); // 첫 페이지 여부
            response.put("isLast", meetBoardPage.isLast()); // 마지막 페이지 여부

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("게시글 리스트 조회 오류: " + e.getMessage());
            throw new BoardException(e.getMessage());
        }
    }

    //모임 게시판 상세 조회
    @Override
    @GetMapping("/{meetBoardId}")
    public ResponseEntity<?> getDetail(@PathVariable Long meetBoardId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("email : " + email);
            MeetBoardDetailsDTO meetBoardDetailsDTO = meetBoardService.getDetails(email,meetBoardId);
            MeetBoardPermissionDTO meetBoardPermission = meetBoardService.getPermission(email, meetBoardId);
            ResponseMeetBoardDetailPermissionDTO response = ResponseMeetBoardDetailPermissionDTO.builder()
                    .meetBoardDetailsDTO(meetBoardDetailsDTO)
                    .meetBoardPermissionDTO(meetBoardPermission)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            throw new BoardException(e.getMessage());
        }
    }

    //모임 게시물 등록
    @Override
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> makeBoard(@Validated @RequestPart("meetBoard") RequestMeetBoardDTO meetBoard,
                                       @RequestPart(value = "images" , required = false) List<MultipartFile> images,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("postBoard 컨트럴러 입장");
        try {
            if (bindingResult.hasErrors()) {
                log.error("binding error : {}", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }
            MeetBoardServiceDTO meetBoardServiceDTO = MeetBoardServiceDTO.makeServiceDTO(meetBoard,images);
            ResponseMeetBoardDTO response = meetBoardService.postBoard(meetBoardServiceDTO, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            throw new BoardException(e.getMessage());
        }
    }

    @Override
    @DeleteMapping("{meetId}/{meetBoardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long meetId, @PathVariable Long meetBoardId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String response = meetBoardService.deleteBoard(meetId, meetBoardId, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("모임 게시글 삭제 오류: " + e.getMessage());
            throw new BoardException(e.getMessage());
        }
    }

    @Override
    @PutMapping("/{meetBoardId}")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long meetBoardId,
            @Valid  @RequestPart("updateBoard") UpdateRequestMeetBoardDTO requestMeetBoardDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 입력값 검증 예외가 발생하면 예외 메세지를 출력
            if (bindingResult.hasErrors()) {
                log.error("binding error: {}", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }

            UpdateMeetBoardServiceDTO updateMeetBoardServiceDTO = UpdateMeetBoardServiceDTO.makeServiceDTO(meetBoardId,requestMeetBoardDTO,images);
            ResponseMeetBoardDTO response = meetBoardService.updateBoardService(updateMeetBoardServiceDTO, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("모임 게시글 수정 오류 " + e.getMessage());
            throw new BoardException(e.getMessage());
        }
    }

}
