package com.example.meettify.repository.community;

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
    @Query("UPDATE communities c SET c.viewCount = c.viewCount + :increment WHERE c.communityId = :communityId")
    @Transactional
    void incrementViewCount(@Param("communityId") Long communityId, @Param("increment") int increment);

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
}
