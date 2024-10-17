package com.example.meettify.repository.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query(value = "select c from comments c" +  // 엔티티 이름 사용
            " join fetch c.community cm" +  // fetch로 관계 조인
            " where cm.communityId = :communityId" +  // 엔티티 필드 접근
            " order by c.commentId desc",
            countQuery = "select count(c) from comments c" +  // 엔티티 이름 사용
                    " join c.community cm" +  // 동일한 조인 사용
                    " where cm.communityId = :communityId")
    Page<CommentEntity> findAll(Pageable pageable, @Param("communityId") Long communityId);
    }

