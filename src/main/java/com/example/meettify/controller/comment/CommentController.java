package com.example.meettify.controller.comment;

import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/{communityId}/comment")
public class CommentController implements CommentControllerDocs{
    private final CommentService commentService;

    // 댓글 생성
    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
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

    // 댓글 수정
    @Override
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @PathVariable Long communityId,
                                           @RequestBody UpdateCommentDTO comment,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseCommentDTO response = commentService.updateComment(commentId, communityId, comment, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 댓글 삭제
    @Override
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            String response = commentService.deleteComment(commentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable Long communityId) {
        try {
            ResponseCommentDTO response = commentService.getComment(communityId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @GetMapping("/{commentId}/commentList")
    public ResponseEntity<?> getComments(@PathVariable Long communityId, Pageable page) {
        try {
            Page<ResponseCommentDTO> comments = commentService.getComments(page, communityId);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("comments", comments.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", comments.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", comments.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", comments.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", comments.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", comments.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", comments.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", comments.isLast());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
