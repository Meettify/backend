package com.example.meettify.dto.meetBoard;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;
/*
 *   worker  : 조영흔
 *   work    : 회원가입시 프론트가 서버로 보내주는 request
 *   date    : 2024/09/30
 * */
@Builder
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateRequestMeetBoardDTO {

    @Schema(description = "게시판 제목 ", example = "운동 모임 후기 수장 제목")
    @Length(min = 1, max = 80)
    String meetBoardTitle;

    @Schema(description = "게시판 수정 내용", example = "우리는 어쩌구 저쩌구 수정본")
    @Pattern(regexp = "^[\\S\\s]{1,2500}$", message = "컨텐츠은 1자 이상 2500자 이하이어야 합니다.")
    String meetBoardContent;

    @Schema(description = "기존 이미지", example = "main.jpg")
    @Builder.Default
    private List<String> imagesUrl = new ArrayList<>();
}
