package com.example.meettify.dto.board;

import com.example.meettify.entity.notice.NoticeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseNoticeDTO {
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

    // 공지사항 엔티티를 DTO로 변환하는 작업
    public static ResponseNoticeDTO changeNotice(NoticeEntity notice) {
        return ResponseNoticeDTO.builder()
                .boardId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .nickName(notice.getMember().getNickName())
                .regTime(notice.getRegTime())
                .build();
    }
}
