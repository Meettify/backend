package com.example.meettify.repository.question;

import com.example.meettify.dto.question.ResponseCountDTO;
import com.example.meettify.entity.question.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomQuestionRepository {
    Page<QuestionEntity> findAllQuestions(Pageable page);
    ResponseCountDTO getQuestionStatistics(String email);

}
