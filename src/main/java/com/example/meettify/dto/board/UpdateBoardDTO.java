package com.example.meettify.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/*
 *   worker : 유요한
 *   work   : 게시글을 수정할 때 사용할 때 프론트에게 받는 클래스
 *   date   : 2024/10/09
 * */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UpdateBoardDTO {
    @Schema(description = "문의글 제목")
    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    @Schema(description = "문의글 본문")
    private String content;

    @Schema(description = "남길 상품 이미지")
    private List<Long> remainImgId; // 남길 이미지
}
