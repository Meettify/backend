package com.example.meettify.controller.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "주문하기", description = "주문하기 API")
public interface QuestionControllerDocs {
    @Operation(summary = "문의하기", description = "문의하는 API")
    ResponseEntity<?> saveQuestion(CreateBoardDTO question, UserDetails userDetails);
}
