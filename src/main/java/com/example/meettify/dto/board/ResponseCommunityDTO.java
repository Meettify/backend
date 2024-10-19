package com.example.meettify.dto.board;

import com.example.meettify.entity.community.CommunityEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseCommunityDTO {
    @Schema(description = "게시글 번호", example = "1")
    private Long boardId;

    @Schema(description = "게시글 제목")
    @NotNull(message = "게시글 제목은 필 수 입력입니다.")
    private String title;

    @Schema(description = "게시글 본문")
    private String content;

    @Schema(description = "유저 닉네임")
    private String nickName;

    @Schema(description = "게시글 작성 시간")
    private LocalDateTime regTime;

    @Schema(description = "게시글 이미지")
    @Builder.Default
    private List<ResponseBoardImgDTO> images = new ArrayList<>();

    @Schema(description = "조회수")
    private int viewCount;


    // 커뮤니티 엔티티를 DTO로 변환하는 작업
    public static ResponseCommunityDTO changeCommunity(CommunityEntity community) {
        List<ResponseBoardImgDTO> images = community.getImages().stream()
                .map(ResponseBoardImgDTO::changeDTO)
                .toList();

        return ResponseCommunityDTO.builder()
                .boardId(community.getCommunityId())
                .title(community.getTitle())
                .content(community.getContent())
                .nickName(community.getMember().getNickName())
                .regTime(community.getRegTime())
                .images(images)
                .viewCount(community.getViewCount())
                .build();
    }

    // 조회수를 레디스와 합쳐서 최신화한 다음 반환
    public static ResponseCommunityDTO communityDetail(CommunityEntity community, int viewCount) {
        List<ResponseBoardImgDTO> images = community.getImages().stream()
                .map(ResponseBoardImgDTO::changeDTO)
                .toList();

        return ResponseCommunityDTO.builder()
                .boardId(community.getCommunityId())
                .title(community.getTitle())
                .content(community.getContent())
                .nickName(community.getMember().getNickName())
                .regTime(community.getRegTime())
                .images(images)
                .viewCount(viewCount)
                .build();
    }
}
