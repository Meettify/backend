package com.example.meettify.repository.member;

import com.example.meettify.entity.member.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
    Page<MemberEntity> findAll(Pageable pageable, String memberEmail);
}
