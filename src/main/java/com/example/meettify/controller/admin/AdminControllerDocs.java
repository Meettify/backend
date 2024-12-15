package com.example.meettify.controller.admin;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.dto.question.ReplyStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "관리자 컨트롤러", description = "관리자 기능")
public interface AdminControllerDocs {
    @Operation(summary = "회원 리스트", description = "전체적인 회원들을 가져오는 API")
    ResponseEntity<?> getAllMembers(Pageable page, String memberEmail);

    @Operation(summary = "문의글 내역 보기", description = "전체적인 문의글 내역 가져오는 API")
    ResponseEntity<?> getAllQuestions(Pageable page, ReplyStatus replyStatus);

    @Operation(summary = "문의글 답변 달기", description = "문의글에 답변 다는 API")
    ResponseEntity<?> createAnswer(Long questionId, CreateAnswerDTO answer, UserDetails userDetails);

    @Operation(summary = "문의글 답변 수정", description = "문의글 답변 수정 API")
    ResponseEntity<?> updateAnswer(Long answerId, UpdateCommentDTO answer);

    @Operation(summary = "문의글 답변 삭제", description = "문의글 답변 삭제 API")
    ResponseEntity<?> deleteAnswer(Long answerId);

    @Operation(summary = "회원 추방", description = "회원 추방 API")
    ResponseEntity<?> removeMember(Long memberId);

}
