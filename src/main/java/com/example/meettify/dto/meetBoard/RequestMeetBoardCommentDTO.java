package com.example.meettify.dto.meetBoard;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDateTime;

/*
 *   worker  : 조영흔
 *   work    : 모임 Comment작성시 프론트가 보내주는 DTO
 *   date    : 2024/10/03
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestMeetBoardCommentDTO {

    @Schema(description = "게시판 번호", example = "1")
    @NotNull(message = "작성할 게시판 번호는 필수입니다.")
    private Long MeetBoardId;

    @Schema(description = "대댓글의 경우 부모를 보내줘야함", example = "1")
    @Null(message = "대댓글일때만 값이 있음")
    private Long parentComment;

    @Schema(description = "댓글 내용", example = "정말 즐거운 댓글")
    @NotNull(message = "댓글 내은 필수입니다.")
    private String content;

    @Schema(description = "댓글 작성 시간")
    @Null
    private LocalDateTime postDate;


}
