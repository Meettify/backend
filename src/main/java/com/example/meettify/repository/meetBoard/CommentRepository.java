package com.example.meettify.repository.meetBoard;


import com.example.meettify.entity.meetBoard.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // 특정 게시글에 속한 댓글 목록 조회
    List<CommentEntity> findByMeetBoardEntity_MeetBoardId(Long meetBoardId);

    // 특정 부모 댓글의 대댓글 목록 조회
    List<CommentEntity> findByParentComment_CommentId(Long parentCommentId);

    // 특정 게시글에 속한 부모 댓글(대댓글이 아닌) 목록 조회
    List<CommentEntity> findByMeetBoardEntity_MeetBoardIdAndParentCommentIsNull(Long meetBoardId);
}