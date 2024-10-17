package com.example.meettify.repository.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "select c from comments c" +
            " join fetch communities c2" +
            " where c2.communityId == :communityId" +
            " order by c.commentId desc ",
    countQuery = "select count(c) from comments c where c.commentId == :communityId")
    Page<CommentEntity> findAll(Pageable pageable, @Param("communityId") Long communityId);
}
