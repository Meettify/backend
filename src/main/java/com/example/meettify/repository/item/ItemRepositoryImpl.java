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

import java.util.List;

import static com.example.meettify.entity.item.QItemEntity.itemEntity;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements CustomItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemEntity> itemsSearch(ItemSearchCondition condition, Pageable pageable) {
        JPAQuery<ItemEntity> content = queryFactory
                .selectFrom(itemEntity)
                .where(nameEq(condition.getName()),
                        statusEq(condition.getStatus()),
                        categoryEq(condition.getCategory()),
                        priceEq(condition.getMinPrice(), condition.getMaxPrice()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // count 쿼리 : 상품의 총 개수를 가져오기 위해서
        JPAQuery<Long> count = queryFactory
                .select(itemEntity.count())
                .from(itemEntity)
                .where(nameEq(condition.getName()),
                        statusEq(condition.getStatus()),
                        categoryEq(condition.getCategory()),
                        priceEq(condition.getMinPrice(), condition.getMaxPrice()));

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
    private BooleanExpression nameEq(String name) {
        // likeIgnoreCase는 QueryDsl에서 문자열에 대한 대소문자를 무시하고 부분 일치 검색을 수행하는 메서드
        return hasText(name) ? itemEntity.itemName.likeIgnoreCase("%" + name + "%") : null;
    }

    // 상품 상태에 따른 검색
    private BooleanExpression statusEq(ItemStatus status) {
        return hasText(String.valueOf(status)) ? itemEntity.itemStatus.eq(status) : null;
    }

    // 상품 카테고리별 검색
    private BooleanExpression categoryEq(Category category) {
        return hasText(String.valueOf(category)) ? itemEntity.itemCategory.eq(category) : null;
    }

    // 이상
    private BooleanExpression priceGoe(int startPrice) {
        return startPrice != 0 ? itemEntity.itemPrice.goe(startPrice) : null;
    }

    // 이하
    private BooleanExpression priceLoe(int endPrice) {
        return endPrice != 0 ? itemEntity.itemPrice.loe(endPrice) : null;
    }

    private BooleanBuilder priceEq(int startPrice, int endPrice) {
        BooleanBuilder builder = new BooleanBuilder();
        if (startPrice != 0) {
            builder.and(priceGoe(startPrice));
        }
        if (endPrice != 0) {
            builder.and(priceLoe(endPrice));
        }
        return builder;
    }
}
