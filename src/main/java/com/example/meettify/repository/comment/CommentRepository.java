package com.example.meettify.repository.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
