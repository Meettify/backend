package com.example.meettify.dto.meetBoard;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class UpdateMeetBoardCommentDTO {
    @Schema(description = "모임 게시판 댓글 수정")
    private String comment;
}
