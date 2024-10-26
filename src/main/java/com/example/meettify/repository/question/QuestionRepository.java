package com.example.meettify.repository.question;

import com.example.meettify.entity.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    @Query("select q from questions q" +
            " join fetch q.member" +
            " where q.questionId = :questionId")
    Optional<QuestionEntity> findByQuestionId(@Param("questionId") Long questionId);
}
