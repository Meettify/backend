package com.example.meettify.repository.community;

import com.example.meettify.entity.community.CommunityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {
    // 커뮤니티 페이징 처리
    @Query(value = "select c from communities c" +
    " join fetch c.member " +
    " order by c.communityId desc ",
    countQuery = "select count(c) from communities c")
    Page<CommunityEntity> findAll(Pageable pageable);

    // 커뮤니티 검색
    @Query(value = "select c from communities c" +
    " join fetch c.member" +
    " where (:searchTitle is null or c.title like %:searchTitle%)" +
    " order by c.communityId desc ",
    countQuery = "select count(c) from communities c where (:searchTitle is null or c.title like %:searchTitle%)")
    Page<CommunityEntity> findBySearchTitle(Pageable pageable, String searchTitle);
}
