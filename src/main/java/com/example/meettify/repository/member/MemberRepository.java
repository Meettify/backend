package com.example.meettify.repository.member;

import com.example.meettify.dto.member.role.UserRole;
import com.example.meettify.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByMemberEmail(String memberEmail);
    boolean existsByMemberEmail(String memberEmail);
    boolean existsByNickName(String nickName);
    MemberEntity findByNickName(String nickName);
    List<MemberEntity> findAllByMemberRole(UserRole role);
}
