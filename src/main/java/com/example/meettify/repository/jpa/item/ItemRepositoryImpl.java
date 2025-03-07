package com.example.meettify.repository.jpa.item;

import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.exception.item.ItemException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.meettify.entity.item.QItemEntity.itemEntity;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Log4j2
public class ItemRepositoryImpl implements CustomItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemEntity> itemsSearch(ItemSearchCondition condition, Pageable pageable) {
        try {

            log.info("offset: {}, size: {}", pageable.getOffset(), pageable.getPageSize());

            JPAQuery<ItemEntity> content = queryFactory
                    .selectFrom(itemEntity)
                    .where(titleEq(condition.getTitle()),
                            statusEq(condition.getStatus()),
                            priceEq(condition.getMinPrice(), condition.getMaxPrice()),
                            categoryEq(condition.getCategory()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
            log.debug("Content query: {}", content.fetch());

            // count 쿼리 : 상품의 총 개수를 가져오기 위해서
            JPAQuery<Long> count = queryFactory
                    .select(itemEntity.count())
                    .from(itemEntity)
                    .where(titleEq(condition.getTitle()),
                            statusEq(condition.getStatus()),
                            priceEq(condition.getMinPrice(), condition.getMaxPrice()),
                            categoryEq(condition.getCategory()));

            log.info("count: {}", count::fetchOne);


            dynamicSort(pageable, content);
            List<ItemEntity> result = content.fetch();

            // 결과 리스트의 크기를 체크하여 적절한 처리
            if (result.isEmpty()) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0); // 빈 페이지 반환
            }


            // 페이지 시작이거나 컨텐츠의 사이즈가 페이지 사이즈보다 작거나
            // 마지막 페이지일 때 카운트 쿼리를 호출하지 않는다.
            return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
        } catch (Exception e) {
            log.error("Index out of bounds while fetching items: " + e.getMessage());
            throw new ItemException("상품을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }

    private static void dynamicSort(Pageable pageable, JPAQuery<ItemEntity> content) {
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
    }

    private BooleanExpression titleEq(String title) {
        return (!hasText(title)) ? null : itemEntity.itemName.likeIgnoreCase("%" + title + "%");
    }

    private BooleanExpression statusEq(ItemStatus status) {
        return status == null ? null : itemEntity.itemStatus.eq(status);
    }

    private BooleanExpression categoryEq(Category category) {
        return category == null ? null : itemEntity.itemCategory.eq(category);
    }


    private BooleanBuilder priceEq(int startPrice, int endPrice) {
        BooleanBuilder builder = new BooleanBuilder();

        // 둘 다 0일 경우, 조건 없이 검색
        if (startPrice == 0 && endPrice == 0) {
            return builder; // 모든 상품 검색
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
        return builder;
    }

    // 이상
    private BooleanExpression priceGoe(int startPrice) {
        return itemEntity.itemPrice.goe(startPrice);
    }

    // 이하
    private BooleanExpression priceLoe(int endPrice) {
        return itemEntity.itemPrice.loe(endPrice);
    }

    public long countItems(ItemSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(titleEq(condition.getTitle()))
                .and(statusEq(condition.getStatus()))
                .and(categoryEq(condition.getCategory()))
                .and(priceEq(condition.getMinPrice(), condition.getMaxPrice()));

        return queryFactory
                .select(itemEntity.count())
                .from(itemEntity)
                .where(builder)
                .fetchOne();
    }

    // 카테고리 별로 키워드를 동적으로 조회
    @Override
    public List<ItemEntity> findItemsByCategoriesAndKeyword(Set<Category> categories, String keyword) {
        try {
            // 카테고리와 키워드에 해당하는 상품을 찾기 위한 로직
            return queryFactory
                    .selectFrom(itemEntity)
                    .where(itemEntity.itemCategory.in(categories)
                            ,keywordContains(keyword))
                    .fetch();
        } catch (Exception e) {
            log.error("Index out of bounds while fetching items: " + e.getMessage());
            throw new ItemException("상품을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }
    private BooleanExpression keywordContains(String keyword) {
        return keyword == null || keyword.isEmpty() ? null :
                itemEntity.itemName.contains(keyword).or(itemEntity.itemDetails.contains(keyword));
    }
    
    // 상품 상태가 WAIT인 것을 전부 조회
    @Override
    public Page<ItemEntity> findAllByWait(Pageable page) {
        try {
            JPAQuery<ItemEntity> content = queryFactory
                    .selectFrom(itemEntity)
                    .where(itemEntity.itemStatus.eq(ItemStatus.WAIT));

            JPAQuery<Long> count = queryFactory
                    .select(itemEntity.count())
                    .from(itemEntity)
                    .where(itemEntity.itemStatus.eq(ItemStatus.WAIT));

            dynamicSort(page, content);
            List<ItemEntity> result = content.fetch();

            // 페이지 시작이거나 컨텐츠의 사이즈가 페이지 사이즈보다 작거나
            // 마지막 페이지일 때 카운트 쿼리를 호출하지 않는다.
            return PageableExecutionUtils.getPage(result, page, count::fetchOne);
        } catch (Exception e) {
            log.error("Index out of bounds while fetching items: " + e.getMessage());
            throw new ItemException("상품을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }

    @Override
    public long countAll() {
        try {
            return Optional.ofNullable(
                    queryFactory
                            .select(itemEntity.count())
                            .from(itemEntity)
                            .fetchOne()
            ).orElse(0L);
        } catch (Exception e) {
            throw new ItemException("상품을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }
}
