package com.example.meettify.entity.answer;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import jakarta.persistence.*;
import lombok.*;



/*
 *   worker  : 유요한
 *   work    : 문의글 답변 댓글 엔티티
 *   date    : 2024/11/15
 * */
@Entity(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AnswerCommentEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_comment_id")
    private Long answerId;

    @Column(nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    private String writerEmail;

    // 생성
    public static AnswerCommentEntity saveComment(CreateAnswerDTO comment,
                                                  MemberEntity member,
                                                  QuestionEntity question,
                                                  String email) {
        return AnswerCommentEntity.builder()
                .answer(comment.getComment())
                .member(member)
                .question(question)
                .writerEmail(email)
                .build();
    }

    // 수정
    public void updateComment(UpdateCommentDTO comment) {
        this.answer = comment.getComment();
    }


}
