package com.example.meettify.entity.answer;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/*
 *   worker  : 유요한
 *   work    : 문의글 답변 댓글 엔티티
 *   date    : 2024/11/15
 * */
@Entity(name = "answer")
@Getter
@ToString(exclude = {"member", "community", "parent", "children"})
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

    // 생성
    public static AnswerCommentEntity saveComment(CreateAnswerDTO comment,
                                                  MemberEntity member,
                                                  QuestionEntity question) {
        return AnswerCommentEntity.builder()
                .answer(comment.getComment())
                .member(member)
                .question(question)
                .build();
    }

    // 수정
    public void updateComment(UpdateCommentDTO comment) {
        this.answer = comment.getComment();
    }


}
