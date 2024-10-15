package com.example.meettify.repository.item;

import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.item.QItemEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.meettify.entity.item.QItemEntity.itemEntity;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements CustomItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemEntity> itemsSearch(ItemSearchCondition condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(titleEq(condition.getTitle()))
                .and(statusEq(condition.getStatus()))
                .and(categoryEq(condition.getCategory()))
                .and(priceEq(condition.getMinPrice(), condition.getMaxPrice()));


        JPAQuery<ItemEntity> content = queryFactory
                .selectFrom(itemEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // count 쿼리 : 상품의 총 개수를 가져오기 위해서
        JPAQuery<Long> count = queryFactory
                .select(itemEntity.count())
                .from(itemEntity)
                .where(builder);

        for (Sort.Order order : pageable.getSort()) {
            // PathBuilder는 주어진 엔티티의 동적인 경로를 생성하는데 사용된다.
            PathBuilder pathBuilder = new PathBuilder(
                    // 엔티티의 타입 정보를 얻어옴
                    itemEntity.getType(),
                    // 엔티티의 메타데이터를 얻어온다.
                    itemEntity.getMetadata());
            // Order 객체에서 정의된 속성에 해당하는 동적 경로를 얻어온다.
            // 예를 들어, 만약 order.getProperty()가 memberName이라면
            // pathBuilder.get("memberName")은 엔티티의 memberName 속성에 대한 동적 경로를 반환
            // 이 동적 경로는 QueryDsl에서 사용되어 정렬 조건을 만들 때 활용된다.
            PathBuilder sort = pathBuilder.get(order.getProperty());

            content.orderBy(
                    new OrderSpecifier<>(
                            order.isDescending() ? Order.DESC : Order.ASC,
                            sort != null ? sort : itemEntity.itemId
                    ));
        }
        List<ItemEntity> result = content.fetch();
        // 페이지 시작이거나 컨텐츠의 사이즈가 페이지 사이즈보다 작거나
        // 마지막 페이지일 때 카운트 쿼리를 호출하지 않는다.
        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    // 조건을 동적으로 처리하기 위해
    private BooleanExpression titleEq(String title) {
        // likeIgnoreCase는 QueryDsl에서 문자열에 대한 대소문자를 무시하고 부분 일치 검색을 수행하는 메서드
        return hasText(title) ? itemEntity.itemName.likeIgnoreCase("%" + title + "%") : null;
    }

    // 상품 상태에 따른 검색
    private BooleanExpression statusEq(ItemStatus status) {
        return hasText(String.valueOf(status)) ? itemEntity.itemStatus.eq(status) : null;
    }

    // 상품 카테고리별 검색
    private BooleanExpression categoryEq(Category category) {
        return hasText(String.valueOf(category)) ? itemEntity.itemCategory.eq(category) : null;
    }

    private BooleanBuilder priceEq(int startPrice, int endPrice) {
        BooleanBuilder builder = new BooleanBuilder();

        // 둘 다 0일 경우, 조건 없이 검색
        if (startPrice == 0 && endPrice == 0) {
            return null; // 모든 상품 검색
        }

        // 가격 시작 조건이 유효한 경우 추가
        if (startPrice > 0) { // startPrice가 0보다 클 경우에만 추가
            builder.and(priceGoe(startPrice));
        }

        // 가격 끝 조건이 유효한 경우 추가
        if (endPrice > 0) { // endPrice가 0보다 클 경우에만 추가
            builder.and(priceLoe(endPrice));
        }

        // 조건이 없을 경우 null 반환
        return builder.hasValue() ? builder : null;
    }

    // 이상
    private BooleanExpression priceGoe(int startPrice) {
        return itemEntity.itemPrice.goe(startPrice);
    }

    // 이하
    private BooleanExpression priceLoe(int endPrice) {
        return itemEntity.itemPrice.loe(endPrice);
    }
}
