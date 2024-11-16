package com.example.meettify.service.answer;

import com.example.meettify.dto.comment.CreateAnswerDTO;
import com.example.meettify.dto.comment.CreateCommentDTO;
import com.example.meettify.dto.comment.ResponseCommentDTO;
import com.example.meettify.entity.answer.AnswerCommentEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.comment.CommentException;
import com.example.meettify.repository.answer.AnswerCommentRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.question.QuestionRepository;
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

    @Override
    public ResponseCommentDTO createAnswerComment(Long questionId, CreateAnswerDTO answer, String email) {
        try {
            // 관리자 회원 조회
            MemberEntity findAdmin = memberRepository.findByMemberEmail(email);
            // 문의글 조회
            QuestionEntity findQuestion = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BoardException("문의글이 존재하지 않습니다."));

            AnswerCommentEntity makeAnswer = AnswerCommentEntity.saveComment(answer, findAdmin, findQuestion);
            AnswerCommentEntity saveAnswer = answerCommentRepository.save(makeAnswer);
            return ResponseCommentDTO.changeDTO(saveAnswer, findAdmin.getNickName());
        } catch (Exception e) {
            throw new CommentException("답변 생성하는데 실패했습니다.");
        }
    }
}
