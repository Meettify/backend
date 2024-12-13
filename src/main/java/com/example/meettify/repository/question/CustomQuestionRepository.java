package com.example.meettify.repository.question;

import com.example.meettify.dto.question.ReplyStatus;
import com.example.meettify.dto.question.ResponseCountDTO;
import com.example.meettify.entity.question.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CustomQuestionRepository {
    Page<QuestionEntity> findAllQuestions(Pageable page, ReplyStatus replyStatus);
    ResponseCountDTO countMyQuestions(String email);
    ResponseCountDTO countAllQuestions();
    Page<QuestionEntity> findAllByMember(String memberEmail, Pageable pageable, ReplyStatus replyStatus);
}
