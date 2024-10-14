package com.example.meettify.controller.meetBoard;

import com.example.meettify.dto.meetBoard.RequestMeetBoardCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "meetBoardComment", description = "모임 게시판 댓글 API")
public interface MeetBoardCommentControllerDocs {

    @Operation(summary = "댓글 등록", description = "댓글 등록하는 API")
    public ResponseEntity<?> postMeetBoardComment(
            @PathVariable Long meetId,
            @PathVariable Long meetBoardId,
            @Validated @RequestBody RequestMeetBoardCommentDTO requestMeetBoardCommentDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "댓글 삭제", description = "댓글 삭제하는 API")
    public ResponseEntity<?> deleteMeetBoardComment(@PathVariable Long meetBoardCommentId, @AuthenticationPrincipal UserDetails userDetails);


}
