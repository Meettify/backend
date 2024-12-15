package com.example.meettify.repository.member;

import com.example.meettify.entity.member.BannedMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedMemberRepository extends JpaRepository<BannedMemberEntity, Long> {
    BannedMemberEntity findByMemberEmail(String email);
}
