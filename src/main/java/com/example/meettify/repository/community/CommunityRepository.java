package com.example.meettify.repository.community;

import com.example.meettify.entity.community.CommunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {
}
