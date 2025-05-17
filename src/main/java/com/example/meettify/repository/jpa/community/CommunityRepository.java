package com.example.meettify.repository.jpa.community;

import com.example.meettify.entity.community.CommunityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    Page<CommunityEntity> findBySearchTitle(Pageable pageable, @Param("searchTitle") String searchTitle);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE communities c SET c.viewCount = c.viewCount + :viewCount WHERE c.communityId = :communityId")
    @Transactional
    void incrementViewCount(@Param("communityId") Long communityId, @Param("viewCount") int viewCount);

    @Query("SELECT c.communityId FROM communities c")
    List<Long> findAllCommunityIds();

    @Query("select c from communities c" +
            " join fetch c.member" +
            " where c.communityId = :communityId")
    CommunityEntity findByCommunityId(@Param("communityId") Long communityId);

    @Query(value = "SELECT c FROM communities c " +
            "JOIN FETCH c.member m " +
            "WHERE m.memberEmail = :memberEmail " +
    "order by c.communityId desc ",
    countQuery = "select count(c) from communities c where c.member.memberEmail = :memberEmail")
    Page<CommunityEntity> findAllByMemberEmail(@Param("memberEmail") String memberEmail, Pageable pageable);

    long countByMemberMemberEmail(String memberEmail);

    @Query("SELECT COUNT (c) from communities c")
    long countAllItems();

    List<CommunityEntity> findTop10ByOrderByViewCountDesc();

    @Query("select c from communities c " +
            "join fetch c.member " +
            "where c.communityId in :communityIds")
    List<CommunityEntity> findByTopCommunityIds(@Param("communityIds") List<Long> topCommunityIds);
}
