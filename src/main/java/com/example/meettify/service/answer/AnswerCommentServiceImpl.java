package com.example.meettify.service.answer;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.ResponseAnswerCommentDTO;
import com.example.meettify.dto.comment.UpdateCommentDTO;
import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.repository.jpa.answer.AnswerCommentRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AnswerCommentServiceImpl implements AnswerCommentService {
    private final AnswerCommentRepository answerCommentRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    // 답변하기
    @Override
    public ResponseAnswerCommentDTO createAnswerComment(Long questionId, CreateAnswerDTO answer, String email) {
        try {
            // 관리자 회원 조회
            MemberEntity findAdmin = memberRepository.findByMemberEmail(email);
            // 문의글 조회
            QuestionEntity findQuestion = questionRepository.findByQuestionId(questionId)
                    .orElseThrow(() -> new BoardException("문의글이 존재하지 않습니다."));

            // 답변 엔티티 생성
            AnswerCommentEntity makeAnswer = AnswerCommentEntity.saveComment(
                    answer, findAdmin, findQuestion, findQuestion.getMember().getNickName());
            // 답변 저장
            AnswerCommentEntity saveAnswer = answerCommentRepository.save(makeAnswer);

            // 문의글에 답변 달려서 문의글 상태를 변경
            findQuestion.changeReplyO();

            return ResponseAnswerCommentDTO.createResponse(saveAnswer, findQuestion.getMember().getMemberEmail());
        } catch (Exception e) {
            throw new CommentException("답변 생성하는데 실패했습니다.");
        }
    }

    // 답변 수정
    @Override
    public ResponseAnswerCommentDTO updateAnswerComment(Long answerId, UpdateCommentDTO answer) {
        try {
            AnswerCommentEntity findAnswer = answerCommentRepository.findByAnswerId(answerId)
                    .orElseThrow(() -> new CommentException("답변이 존재하지 않습니다."));
            // 답변 수정
            findAnswer.updateComment(answer);
            return ResponseAnswerCommentDTO.updateResponse(findAnswer, findAnswer.getMember().getNickName());
        } catch (Exception e) {
            throw new CommentException("답변을 수정하는데 실패했습니다.");
        }
    }

    // 답변 삭제
    @Override
    public String deleteAnswerComment(Long answerId) {
        try {
            AnswerCommentEntity findAnswer = answerCommentRepository.findByAnswerIdForDelete(answerId)
                    .orElseThrow(() -> new CommentException("답변이 존재하지 않습니다."));

            // 답변 삭제
            answerCommentRepository.delete(findAnswer);
            QuestionEntity findQuestion = questionRepository.findById(findAnswer.getQuestion().getQuestionId())
                    .orElseThrow(() -> new BoardException("문의글이 존재하지 않습니다."));
            log.info("findQuestion: {}", findQuestion);
            // 문의글 상태 변경
            findQuestion.changeReplyX();

            return "답변을 삭제하셨습니다.";
        } catch (Exception e) {
            return "답변 삭제 실패";
        }
    }

}
