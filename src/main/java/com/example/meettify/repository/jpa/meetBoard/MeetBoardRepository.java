package com.example.meettify.repository.jpa.meetBoard;

import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MeetBoardRepository extends JpaRepository<MeetBoardEntity, Long> {




    //Todo : join fetch 다시 확인
    @Query("SELECT mb FROM meetBoards mb WHERE mb.meetEntity.meetId = :meetId ORDER BY mb.postDate DESC")
    List<MeetBoardEntity> findTop3MeetBoardEntitiesByMeetId(@Param("meetId") Long meetId, Pageable pageable);

    @Query(
            value = "SELECT mb FROM meetBoards mb JOIN FETCH mb.memberEntity m WHERE mb.meetEntity.meetId = :meetId",
            countQuery = "SELECT COUNT(mb) FROM meetBoards mb WHERE mb.meetEntity.meetId = :meetId"
    )
    Page<MeetBoardEntity> findByMeetIdWithMember(@Param("meetId") Long meetId, Pageable pageable);


    MeetBoardEntity findByMemberEntityMemberId(Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update meetBoards mb set mb.viewCount = mb.viewCount + :viewCount where mb.meetBoardId = :meetBoardId")
    @Transactional
    void incrementViewCount(@Param("meetBoardId") Long id, @Param("viewCount") int viewCount);

    @EntityGraph(attributePaths = "meetEntity")
    Optional<MeetBoardEntity> findByMeetBoardId(Long meetBoardId);
}