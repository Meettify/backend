package com.example.meettify.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/*
 *   worker  : 유요한
 *   work    : 댓글 생성시 프론트에서 json으로 데이터를 넘겨줄것을 받는 클래스
 *   date    : 2024/10/17
 * */
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class CreateCommentDTO {
    @Schema(description = "댓글 등록")
    @NotNull(message = "댓글을 빈값으로는 안됩니다.")
    private String comment;
    @Schema(description = "부모 댓글 번호")
    private Long commentParentId;
}
