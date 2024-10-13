package com.example.meettify.repository.meet;

import com.example.meettify.entity.meet.MeetMemberEntity;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MeetMemberRepository extends JpaRepository<MeetMemberEntity, Long> {

    @Query("SELECT mm FROM meetMembers mm " +
            "JOIN FETCH mm.memberEntity m " +
            "JOIN FETCH mm.meetEntity me " +
            "WHERE m.memberEmail = :email AND me.meetId = :meetId")
    Optional<MeetMemberEntity> findByEmailAndMeetId(@Param("email") String email, @Param("meetId") Long meetId);


    @Query("SELECT mm.meetMemberId FROM meetMembers mm " +
            "WHERE mm.memberEntity.memberEmail = :email")
    Set<Long> findIdByEmail(@Param("email") String email);


    boolean existsByMeetEntityAndMemberEntity(MeetEntity meetEntity, MemberEntity memberEntity);

   // MeetID기준으로 현재 멤버를 가져옴
    @Query("SELECT mm FROM meetMembers mm " +
            "JOIN FETCH mm.memberEntity m " +
            "JOIN FETCH mm.meetEntity me " +
            "WHERE me.meetId = :meetId")
    List<MeetMemberEntity> findMeetMembersWithMeetAndMember(@Param("meetId") Long meetId);

    // 가입한 모임 리스트 가져오기
    @Query("SELECT DISTINCT me FROM meetMembers mm " +
            "JOIN mm.meetEntity me " +
            "JOIN FETCH me.meetImages mI " +
            "WHERE mm.memberEntity.memberEmail = :memberEmail")
    List<MeetEntity> findMeetsByMemberName(@Param("memberEmail") String memberEmail);
}
