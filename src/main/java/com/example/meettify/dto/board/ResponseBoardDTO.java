package com.example.meettify.dto.board;

import com.example.meettify.entity.community.CommunityEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBoardDTO {
    @Schema(description = "문의글 번호", example = "1", required = true)
    private Long boardId;

    @Schema(description = "문의글 제목", required = true)
    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    @Schema(description = "문의글 본문")
    private String content;

    @Schema(description = "유저 닉네임")
    private String nickName;

    @Schema(description = "문의글 작성 시간")
    private LocalDateTime regTime;


    // 엔티티를 DTO로 변환하는 작업
    public static ResponseBoardDTO changeCommunity(CommunityEntity community) {
        return ResponseBoardDTO.builder()
                .boardId(community.getBoardId())
                .title(community.getTitle())
                .content(community.getContent())
                .nickName(community.getMember().getNickName())
                .regTime(community.getRegTime())
                .build();
    }
}
