package com.example.meettify.repository.jpa.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("select c from comments c" +
            " join fetch c.member" +
            " where c.commentId = :commentId")
    Optional<CommentEntity> findById(@Param("commentId") Long commentId);

    // 부모 댓글만 가져오기 (parent is null)
    @Query("select distinct c from comments c " +
            "left join fetch c.children " +
            "join fetch c.member " +
            "where c.community.communityId = :communityId and c.parent is null " +
            "order by c.commentId asc")
    Page<CommentEntity> findParentCommentsByCommunityId(@Param("communityId")Long communityId, Pageable page);

    // 자식 댓글 가져오기
    @Query("select distinct c from comments c " +
            "left join fetch c.children " +
            "join fetch c.member " +
            "where c.parent.commentId in :parentIds " +
            "order by c.commentId asc")
    List<CommentEntity> findChildOfChildren(@Param("parentIds") List<Long> parentIds);
}

