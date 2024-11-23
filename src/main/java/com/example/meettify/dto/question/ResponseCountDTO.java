package com.example.meettify.dto.question;

import lombok.*;
import org.aspectj.weaver.patterns.TypePatternQuestions;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@Getter
public class ResponseCountDTO {
    private long totalQuestions; // 전체 문의글 수
    private long completedReplies; // 답글 완료 수
    private long pendingReplies; // 답글 미완료 수

    public static ResponseCountDTO of(long totalQuestions,
                                      long completedReplies,
                                      long pendingReplies) {
        return ResponseCountDTO.builder()
                .totalQuestions(totalQuestions)
                .completedReplies(completedReplies)
                .pendingReplies(pendingReplies)
                .build();
    }
}
