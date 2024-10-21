package com.example.meettify.repository.comment;

import com.example.meettify.entity.comment.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomCommentRepository {
    Page<CommentEntity> findCommentByCommunityId(Long communityId, Pageable pageable);
}
