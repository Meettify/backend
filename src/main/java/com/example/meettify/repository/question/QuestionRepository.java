package com.example.meettify.repository.question;

import com.example.meettify.entity.question.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long>, CustomQuestionRepository {
    @Query("select q from questions q" +
            " join fetch q.member" +
            " where q.questionId = :questionId")
    Optional<QuestionEntity> findByQuestionId(@Param("questionId") Long questionId);

    @Query(value = "select q from questions q" +
            " join fetch q.member m" +
            " where m.memberEmail = :memberEmail" +
    " order by q.questionId desc ",
    countQuery = "select count(q) from questions q where q.member.memberEmail = :memberEmail")
    Page<QuestionEntity> findAllByMember(@Param("memberEmail") String memberEmail, Pageable pageable);
}
