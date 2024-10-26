package com.example.meettify.service.question;

import com.example.meettify.dto.board.CreateBoardDTO;
import com.example.meettify.dto.board.UpdateQuestionDTO;
import com.example.meettify.dto.question.ResponseQuestionDTO;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.question.QuestionEntity;
import com.example.meettify.exception.board.BoardException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class QuestionServiceImpl implements QuestionService{
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    // 문의글 등록
    @Override
    public ResponseQuestionDTO saveQuestion(CreateBoardDTO question, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            if(findMember == null) {
                throw new MemberException("존재하지 않는 회원입니다.");
            }
            // 디비에 저장하기 위해 엔티티 생성
            QuestionEntity questionEntity = QuestionEntity.saveEntity(question, findMember);
            // 디비 저장
            QuestionEntity saveQuestion = questionRepository.save(questionEntity);
            log.info("저장한 문의글 : " + saveQuestion);
            return ResponseQuestionDTO.changeDTO(saveQuestion, findMember.getNickName());
        } catch (Exception e) {
            throw new BoardException("문의글 등록에 실패했습니다 : " + e.getMessage());
        }
    }

    // 문의글 수정
    @Override
    public ResponseQuestionDTO updateQuestion(Long questionId, UpdateQuestionDTO question, String email) {
        try {
            // 문의글 조회
            QuestionEntity findQuestion = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BoardException("문의글이 존재하지 않습니다."));
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember == null || !findQuestion.getMember().getMemberId().equals(findMember.getMemberId())) {
                throw new MemberException("잘못된 회원 정보이므로 문의글을 수정할 수 없습니다.");
            }
            // 문의글 수정
            findQuestion.updateQuestion(question);
            QuestionEntity updateQuestion = questionRepository.save(findQuestion);
            return ResponseQuestionDTO.changeDTO(updateQuestion, findMember.getNickName());
        } catch (Exception e) {
            throw new BoardException("문의글 수정 실패 : " + e.getMessage());
        }
    }

    // 문의 삭제
    @Override
    public String deleteQuestion(Long questionId) {
        try {
            QuestionEntity findQuestion = questionRepository.findById(questionId)
                    .orElseThrow(() -> new BoardException("문의글이 존재하지 않습니다."));

            questionRepository.deleteById(findQuestion.getQuestionId());
            return "문의글을 삭제했습니다.";
        } catch (Exception e) {
            throw new BoardException("문의글을 삭제하는데 실패했습니다." + e.getMessage());
        }
    }
}
