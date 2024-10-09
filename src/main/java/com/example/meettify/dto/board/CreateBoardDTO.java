package com.example.meettify.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
 *   worker : 유요한
 *   work   : 게시글을 만들 때 필요한 값 즉, 제목과 내용만 받습니다.
 *   date   : 2024/10/09
 * */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CreateBoardDTO {
    @Schema(description = "문의글 제목")
    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    @Schema(description = "문의글 본문")
    private String content;
}
