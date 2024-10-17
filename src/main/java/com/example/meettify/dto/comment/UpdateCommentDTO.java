package com.example.meettify.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class UpdateCommentDTO {
    @Schema(description = "댓글 수정")
    private String comment;
}
