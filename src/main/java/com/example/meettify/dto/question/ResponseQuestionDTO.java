package com.example.meettify.dto.question;

import com.example.meettify.dto.comment.ResponseAnswerCommentDTO;
import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@Getter
public class ResponseQuestionDTO {
    @Schema(description = "문의글 번호")
    private Long questionId;
    @Schema(description = "문의글 제목")
    @NotNull(message = "문의글 제목은 필 수 입력입니다.")
    private String title;

    @Schema(description = "문의글 본문")
    @NotNull(message = "문의글 본문은 필 수 입력입니다.")
    private String content;

    @Schema(description = "유저 닉네임")
    private String nickName;

    @Schema(description = "문의글 작성 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime regTime;

    @Schema(description = "답글상태")
    private ReplyStatus replyStatus;

    @Schema(description = "문의글 답변")
    @Builder.Default
    private List<ResponseAnswerCommentDTO> answerComments = new ArrayList<>();

    // 엔티티를 DTO로 변환
    public static ResponseQuestionDTO changeDTO(QuestionEntity question, String nickName) {
        List<AnswerCommentEntity> answerList = question.getAnswerComments().isEmpty() ? Collections.emptyList() : question.getAnswerComments();
        List<ResponseAnswerCommentDTO> list = answerList.stream()
                .map(answer -> ResponseAnswerCommentDTO.change(answer, nickName))
                .toList();


        return ResponseQuestionDTO.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .regTime(question.getRegTime())
                .replyStatus(ReplyStatus.REPLY_X)
                .nickName(nickName)
                .answerComments(list)
                .build();
    }
}
