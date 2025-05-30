package com.example.meettify.repository.jpa.item;

import com.example.meettify.dto.item.ItemSearchCondition;
import com.example.meettify.dto.item.status.ItemStatus;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.exception.item.ItemException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
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
    public Slice<ItemEntity> itemsSearch(ItemSearchCondition condition, Long lastItemId, int size, String sort) {
        try {
            BooleanBuilder builder = new BooleanBuilder()
                    .and(titleEq(condition.getTitle()))
                    .and(statusEq(condition.getStatus()))
                    .and(priceEq(condition.getMinPrice(), condition.getMaxPrice()))
                    .and(categoryEq(condition.getCategory()));

            // 커서 조건 추가
            if (lastItemId != null) {
                builder.and(itemEntity.itemId.lt(lastItemId));
            }

            JPAQuery<ItemEntity> content = queryFactory
                    .selectFrom(itemEntity)
                    .where(builder)
                    // limit(size + 1)에서 +1을 하는 이유는 바로 다음 페이지가 존재하는지(hasNext)를 판단하기 위함
                    .limit(size + 1);
            log.debug("Content query: {}", content.fetch());

            dynamicSort(sort, content);
            List<ItemEntity> result = content.fetch();

            // Slice 처리
            boolean hasNext = false;
            if(result.size() > size) {
                result.remove(size);    // 마지막 아이템 제거
                hasNext = true;
            }

            return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
        } catch (Exception e) {
            log.error("Index out of bounds while fetching items: " + e.getMessage());
            throw new ItemException("상품을 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }

    private static void dynamicSort(String sortParam, JPAQuery<ItemEntity> content) {
        if (sortParam == null || sortParam.isBlank()) {
            // 기본 정렬
            content.orderBy(itemEntity.itemId.desc());
            return;
        }

        // ex) itemPrice,DESC or itemName,ASC
        String[] parts = sortParam.split(",");
        if (parts.length != 2) {
            content.orderBy(itemEntity.itemId.desc());
            return;
        }

        String sortField = parts[0];
        Order direction = parts[1].equalsIgnoreCase("DESC") ? Order.DESC : Order.ASC;

        // 동적 정렬
        PathBuilder<ItemEntity> pathBuilder = new PathBuilder<>(ItemEntity.class, "itemEntity");

        // ✅ 와일드카드(X) → 명시적 타입 지정(O)
        Expression<Comparable> expression = pathBuilder.getComparable(sortField, Comparable.class);

        // ✅ 제네릭 타입이 맞으므로 컴파일 OK
        content.orderBy(new OrderSpecifier<>(direction, expression));
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
                            , keywordContains(keyword))
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
    public Slice<ItemEntity> findAllByWait(Long lastItemId,
                                          int size,
                                          String sort) {
        try {
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(itemEntity.itemStatus.eq(ItemStatus.WAIT));
            // 커서 조건 추가
            if (lastItemId != null) {
                builder.and(itemEntity.itemId.lt(lastItemId));
            }

            JPAQuery<ItemEntity> content = queryFactory
                    .selectFrom(itemEntity)
                    .where(builder)
                    .limit(size + 1);

            dynamicSort(sort, content);
            List<ItemEntity> result = content.fetch();

            // Slice 처리
            boolean hasNext = false;
            if(result.size() > size) {
                result.remove(size);    // 마지막 아이템 제거
                hasNext = true;
            }

            return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
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
