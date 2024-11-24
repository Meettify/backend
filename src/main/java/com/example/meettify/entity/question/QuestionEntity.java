package com.example.meettify.entity.question;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ReplyStatus;
import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private ReplyStatus replyStatus;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AnswerCommentEntity> answerComments = new ArrayList<>();

    // 엔티티 생성
    public static QuestionEntity saveEntity(CreateBoardDTO question, MemberEntity member) {
        return QuestionEntity.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .member(member)
                .replyStatus(ReplyStatus.REPLY_X)
                .build();
    }

    // 수정하기
    public void updateQuestion(UpdateQuestionDTO question) {
        this.title = question.getTitle();
        this.content = question.getContent();
    }

    // 문의글 상태 바뀌기
    public void changeReplyO() {
        this.replyStatus = ReplyStatus.REPLY_O;
    }
    public void changeReplyX() {
        this.replyStatus = ReplyStatus.REPLY_X;
    }

}
