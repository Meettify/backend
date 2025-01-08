package com.example.meettify.repository.jpa.member;

import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.member.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByMemberEmail(String memberEmail);
    boolean existsByMemberEmail(String memberEmail);
    boolean existsByNickName(String nickName);
    MemberEntity findByNickName(String nickName);
    List<MemberEntity> findAllByMemberRole(UserRole role);
    @Query("select m from members m where m.memberRole = :role")
    Page<MemberEntity> findAll(Pageable pageable, @Param("role") UserRole role);
    @Query("select count (m) from members m")
    Long countByMembers();
}
