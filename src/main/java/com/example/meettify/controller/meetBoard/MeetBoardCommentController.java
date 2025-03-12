package com.example.meettify.controller.meetBoard;


import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.dto.meetBoard.RequestMeetBoardCommentDTO;
import com.example.meettify.dto.meetBoard.ResponseMeetBoardCommentDTO;
import com.example.meettify.dto.meetBoard.UpdateMeetBoardCommentDTO;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.service.meetBoard.MeetBoardCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/meetBoardComments")
@RequiredArgsConstructor
public class MeetBoardCommentController implements   MeetBoardCommentControllerDocs{

    private final MeetBoardCommentService meetBoardCommentService;

    //댓글 작성하는 API
    @PostMapping("/meets/{meetId}/boards/{meetBoardId}/comments")
    public ResponseEntity<?> postMeetBoardComment(
            @PathVariable Long meetId,
            @PathVariable Long meetBoardId,
            @Validated @RequestBody RequestMeetBoardCommentDTO requestMeetBoardCommentDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("binding error: {}", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }
            MeetBoardCommentServiceDTO meetBoardServiceCommentDTO = MeetBoardCommentServiceDTO.makeServiceDTO(meetId,meetBoardId,requestMeetBoardCommentDTO);

            ResponseMeetBoardCommentDTO response = meetBoardCommentService.postComment(userDetails.getUsername(), meetBoardServiceCommentDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            throw new CommentException(e.getMessage());
        }
    }

    //댓글 삭제하는 API
    @Override
    @DeleteMapping("/{meetBoardCommentId}")
    public ResponseEntity<?> deleteMeetBoardComment(@PathVariable Long meetBoardCommentId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean isAuthorized = meetBoardCommentService.getPermission(userDetails.getUsername(), meetBoardCommentId).isCanDelete();

            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }

            String response = meetBoardCommentService.deleteComment( meetBoardCommentId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            throw new CommentException(e.getMessage());
        }
    }


    // 모임 게시판 댓글 수정
    @PutMapping("/{meetBoardCommentId}")
    public ResponseEntity<?> updateMeetBoardComment(@PathVariable Long meetBoardCommentId,
                                                    @RequestPart(value = "comment") UpdateMeetBoardCommentDTO updateMeetBoardCommentDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {

        try {
            boolean isAuthorized = meetBoardCommentService.getPermission(userDetails.getUsername(), meetBoardCommentId).isCanEdit();
            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바르지 못한 요청");
            }
            System.out.println(updateMeetBoardCommentDTO.getComment());

            ResponseMeetBoardCommentDTO response = meetBoardCommentService.updateComment(meetBoardCommentId, updateMeetBoardCommentDTO, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            throw new CommentException(e.getMessage());
        }
    }
}









