package com.example.meettify.entity.question;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString
public class QuestionEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(length = 300, nullable = false)
    private String title;
    @Column(length = 3000, nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    // 엔티티 생성
    public static QuestionEntity saveEntity(CreateBoardDTO question, MemberEntity member) {
        return QuestionEntity.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .member(member)
                .build();
    }

    // 수정하기
    public void updateQuestion(UpdateQuestionDTO question) {
        this.title = question.getTitle();
        this.content = question.getContent();
    }
}
