package com.example.meettify.repository.jpa.answer;

import com.example.meettify.entity.answer.AnswerCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnswerCommentRepository extends JpaRepository<AnswerCommentEntity, Long> {
    @Query("select a from answer a " +
    "join fetch a.member m " +
    "where a.answerId = :answerId")
    Optional<AnswerCommentEntity> findByAnswerId(@Param("answerId") Long answerId);

    @Query("select a from answer a " +
            "join fetch a.question q " +
            "where a.answerId = :answerId")
    Optional<AnswerCommentEntity> findByAnswerIdForDelete(@Param("answerId") Long answerId);
}
