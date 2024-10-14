package com.example.meettify.dto.board;

import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.notice.NoticeEntity;
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
public class ResponseBoardDTO {
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


    // 커뮤니티 엔티티를 DTO로 변환하는 작업
    public static ResponseBoardDTO changeCommunity(CommunityEntity community) {
        List<ResponseBoardImgDTO> images = community.getImages().stream()
                .map(ResponseBoardImgDTO::changeDTO)
                .toList();

        return ResponseBoardDTO.builder()
                .boardId(community.getCommunityId())
                .title(community.getTitle())
                .content(community.getContent())
                .nickName(community.getMember().getNickName())
                .regTime(community.getRegTime())
                .images(images)
                .build();
    }

    // 공지사항 엔티티를 DTO로 변환하는 작업
    public static ResponseBoardDTO changeNotice(NoticeEntity notice) {
        return ResponseBoardDTO.builder()
                .boardId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .nickName(notice.getMember().getNickName())
                .regTime(notice.getRegTime())
                .build();
    }
}
