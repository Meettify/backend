package com.example.meettify.repository.meetBoard;

import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetBoardRepository extends JpaRepository<MeetBoardEntity, Long> {




    //Todo : join fetch 다시 확인
    @Query("SELECT mb FROM meetBoards mb WHERE mb.meetEntity.meetId = :meetId ORDER BY mb.postDate DESC")
    List<MeetBoardEntity> findTop3MeetBoardEntitiesByMeetId(@Param("meetId") Long meetId, Pageable pageable);

    @Query("SELECT mb FROM meetBoards mb JOIN FETCH mb.memberEntity m WHERE mb.meetEntity.meetId = :meetId")
    Page<MeetBoardEntity> findByMeetIdWithMember(@Param("meetId") Long meetId, Pageable pageable);


}