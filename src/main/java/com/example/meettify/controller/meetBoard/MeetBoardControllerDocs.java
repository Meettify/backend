package com.example.meettify.controller.meetBoard;


import com.example.meettify.dto.meetBoard.RequestMeetBoardDTO;
import com.example.meettify.dto.meetBoard.UpdateRequestMeetBoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "meetBoard", description = "모임 게시판 API")
public interface MeetBoardControllerDocs {

    @Operation(summary = "모임 게시판 리스트", description = "모임 게시판 List를 페이징 처리와 함께 제공해주기 위한 API")
    public ResponseEntity<?> getList(
            @PathVariable Long meetId,
            Pageable pageable);


    @Operation(summary = "모임 게시물 등록", description = "모임 게시글 등록하기")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> makeBoard(@Validated @RequestPart("meetBoard") RequestMeetBoardDTO meetBoard,
                                       BindingResult bindingResult,
                                       @RequestPart(value = "images" , required = false) List<MultipartFile> images,
                                       @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 게시물 삭제", description = "모임 게시글 삭제하기 ")
    public ResponseEntity<?> deleteBoard(@PathVariable Long meetId, @PathVariable Long meetBoardId, @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 게시물 Detail", description = "모임 게시물 상세 조회 ")
    public ResponseEntity<?> getDetail(@PathVariable Long meetBoardId,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestParam int page,
                                       @RequestParam int size);

    @Operation(summary = "모임 게시판 수정", description = "모임 게시글 수정하기 ")
    @PutMapping("{meetBoardId}")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long meetBoardId,
            @Valid @RequestPart("updateBoard") UpdateRequestMeetBoardDTO requestMeetBoardDTO,
            BindingResult bindingResult,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails);
}
