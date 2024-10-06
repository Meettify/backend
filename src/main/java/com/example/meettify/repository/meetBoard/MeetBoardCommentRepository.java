package com.example.meettify.repository.meetBoard;


import com.example.meettify.entity.meetBoard.MeetBoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetBoardCommentRepository extends JpaRepository<MeetBoardCommentEntity, Long> {

    // 특정 게시글에 속한 댓글 목록 조회
    List<MeetBoardCommentEntity> findByMeetBoardEntity_MeetBoardId(Long meetBoardId);

    // 특정 부모 댓글의 대댓글 목록 조회
    List<MeetBoardCommentEntity> findByParentComment_CommentId(Long parentCommentId);

    // 특정 게시글에 속한 부모 댓글(대댓글이 아닌) 목록 조회
    List<MeetBoardCommentEntity> findByMeetBoardEntity_MeetBoardIdAndParentCommentIsNull(Long meetBoardId);

    @Query("SELECT mc FROM meetBoardComments mc JOIN FETCH mc.meetBoardEntity mb JOIN FETCH mb.meetEntity WHERE mc.commentId = :commentId")
    Optional<MeetBoardCommentEntity> findByIdWithJoin(@Param("commentId") Long commentId);
}