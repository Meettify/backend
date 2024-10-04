package com.example.meettify.controller.meetBoard;


import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.dto.meetBoard.RequestMeetBoardCommentDTO;
import com.example.meettify.dto.meetBoard.ResponseMeetBoardCommentDTO;
import com.example.meettify.service.meetBoard.MeetBoardCommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequestMapping("/api/v1/meetBoardComments")
@RequiredArgsConstructor
public class MeetBoardCommentController {

    private final MeetBoardCommentService meetBoardCommentService;


    @PostMapping
    public ResponseEntity<?> postMeetBoardComment(@Validated @RequestBody RequestMeetBoardCommentDTO requestMeetBoardCommentDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // 입력값 검증 예외가 발생하면 예외 메시지를 출력
            if (bindingResult.hasErrors()) {
                log.error("binding error: {}", bindingResult.getAllErrors());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
            }
            MeetBoardCommentServiceDTO meetBoardServiceCommentDTO  = MeetBoardCommentServiceDTO.makeServiceDTO(requestMeetBoardCommentDTO);
            // 이 회원이 자격이 검증도 같이 서비스에서 진행
            ResponseMeetBoardCommentDTO response = meetBoardCommentService.postComment(userDetails.getUsername(),meetBoardServiceCommentDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("예외 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
