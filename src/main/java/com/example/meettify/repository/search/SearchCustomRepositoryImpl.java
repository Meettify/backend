package com.example.meettify.repository.search;

import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.community.QCommunityEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.item.QItemEntity;
import com.example.meettify.entity.item.QItemImgEntity;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.meet.QMeetEntity;
import com.example.meettify.entity.meet.QMeetImageEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import static org.springframework.util.StringUtils.hasText;


@RequiredArgsConstructor
@Repository
public class SearchCustomRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    // 통합 검색 메서드
    public HashMap<String,List> searchAll(SearchCondition searchCondition) {

        QMeetImageEntity meetImages = QMeetImageEntity.meetImageEntity;
        QItemImgEntity itemImages =  QItemImgEntity.itemImgEntity;

        String keyword = searchCondition.getTotalKeyword();
        List<MeetEntity> meetResults = queryFactory
                .selectFrom(QMeetEntity.meetEntity).distinct()
                .leftJoin(QMeetEntity.meetEntity.meetImages, meetImages).fetchJoin()
                .where(keywordLikeMeet(keyword))
                .orderBy(QMeetEntity.meetEntity.meetId.desc())
                .limit(10)
                .fetch();

        List<ItemEntity> itemResults = queryFactory
                .selectFrom(QItemEntity.itemEntity)
                .leftJoin(QItemEntity.itemEntity.images, itemImages).fetchJoin()
                .where(keywordLikeItem(keyword))
                .orderBy(QItemEntity.itemEntity.itemId.desc())
                .limit(10)
                .fetch();

        List<CommunityEntity> communityResults = queryFactory
                .selectFrom(QCommunityEntity.communityEntity)
                .where(keywordLikeCommunity(keyword))
                .orderBy(QCommunityEntity.communityEntity.regTime.desc())
                .limit(10)
                .fetch();

         HashMap<String,List> response = new HashMap<String, List>();

         response.put("meet",meetResults);
        response.put("item",itemResults);
        response.put("community",communityResults);

        // 결과를 SearchResponseDTO에 매핑하여 반환
        return response;
    }

    // MeetEntity 필터링 조건
    private BooleanExpression keywordLikeMeet(String keyword) {
        return hasText(keyword) ?
                QMeetEntity.meetEntity.meetName.likeIgnoreCase("%" + keyword + "%")
                        .or(QMeetEntity.meetEntity.meetDescription.likeIgnoreCase("%" + keyword + "%")) : null;
    }

    // CommunityEntity 필터링 조건
    private BooleanExpression keywordLikeCommunity(String keyword) {
        return hasText(keyword) ?
                QCommunityEntity.communityEntity.title.likeIgnoreCase("%" + keyword + "%")
                        .or(QCommunityEntity.communityEntity.content.likeIgnoreCase("%" + keyword + "%")) : null;
    }

    // ItemEntity 필터링 조건
    private BooleanExpression keywordLikeItem(String keyword) {
        return hasText(keyword) ?
                QItemEntity.itemEntity.itemName.likeIgnoreCase("%" + keyword + "%")
                        .or(QItemEntity.itemEntity.itemDetails.likeIgnoreCase("%" + keyword + "%")) : null;
    }


}
