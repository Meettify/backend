package com.example.meettify.controller.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController implements CommentControllerDocs{
    private final CommentService commentService;

    @Override
    @PostMapping("/{communityId}")
    public ResponseEntity<?> createComment(@PathVariable Long communityId,
                                           @RequestBody CreateCommentDTO comment,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseCommentDTO response = commentService.createComment(communityId, comment, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
