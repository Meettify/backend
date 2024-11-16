package com.example.meettify.repository.answer;

import com.example.meettify.entity.answer.AnswerCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerCommentRepository extends JpaRepository<AnswerCommentEntity, Long> {
}
