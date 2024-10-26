package com.example.meettify.repository.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long>, CustomCommentRepository {
    @Query("select c from comments c" +
            " join fetch c.member" +
            " where c.commentId = :commentId")
    Optional<CommentEntity> findById(@Param("commentId") Long commentId);
}

