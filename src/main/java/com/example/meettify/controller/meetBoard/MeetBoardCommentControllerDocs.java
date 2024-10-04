package com.example.meettify.controller.meetBoard;

import com.example.meettify.dto.meetBoard.RequestMeetBoardCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "meetBoardComment", description = "모임 게시판 댓글 API")

public interface MeetBoardCommentControllerDocs {

    @Operation(summary = "모임 게시판 댓글 입력", description = "모임 게시판 댓글 입력한다.")
    public ResponseEntity<?> postMeetBoardComment(@Validated @RequestBody RequestMeetBoardCommentDTO requestMeetBoardCommentDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails);
}
