package com.example.meettify.repository.jpa.meet;

import com.example.meettify.dto.meet.MeetSearchCondition;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.meet.MeetEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.meettify.entity.meet.QMeetEntity.meetEntity;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Slf4j
public class MeetRepositoryImpl implements CustomMeetRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 모임 검색 메서드
     * MeetSearchCondition과 Pageable을 받아와 동적으로 조건을 처리하고 페이지네이션과 정렬을 적용하여 모임 정보를 검색합니다.
     */
    @Override
    public Page<MeetEntity> meetsSearch(MeetSearchCondition condition, Pageable pageable) {
        // 메인 검색 쿼리: 모임 엔티티를 검색하며 동적 조건을 적용합니다.
        JPAQuery<MeetEntity> content = queryFactory
                .selectFrom(meetEntity)  // meetEntity를 선택
                .where(
                        nameEq(condition.getName()),        // 이름 조건 추가
                        categoryEq(condition.getCategory()) // 카테고리 조건 추가
                )
                .offset(pageable.getOffset())   // 페이지 시작점 설정
                .limit(pageable.getPageSize()); // 페이지 크기 설정

        // 페이지네이션을 위한 정렬 처리
        for (Sort.Order order : pageable.getSort()) {
            // PathBuilder는 동적으로 경로를 만들기 위해 사용됩니다.
            PathBuilder pathBuilder = new PathBuilder(meetEntity.getType(), meetEntity.getMetadata());
            PathBuilder sort = pathBuilder.get(order.getProperty());

            // 정렬 조건을 OrderSpecifier로 지정
            content.orderBy(new OrderSpecifier<>(
                    order.isDescending() ? Order.DESC : Order.ASC, // 내림차순 또는 오름차순 정렬
                    sort != null ? sort : meetEntity.meetId         // 정렬 대상이 없을 경우 meetId로 기본 정렬
            ));
        }

        // 결과 리스트를 fetch()로 가져옵니다.
        List<MeetEntity> result = content.fetch();

        // 전체 모임 개수를 가져오기 위한 count 쿼리
        JPAQuery<Long> count = queryFactory
                .select(meetEntity.count())  // meetEntity의 총 개수를 선택
                .from(meetEntity)
                .where(
                        nameEq(condition.getName()),        // 이름 조건 추가
                        categoryEq(condition.getCategory()) // 카테고리 조건 추가
                );

        // PageableExecutionUtils를 사용하여 페이지 객체 반환
        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    /**
     * 이름으로 필터링하는 조건을 동적으로 생성합니다.
     * @param name 모임 이름 조건
     * @return BooleanExpression, 이름이 있을 경우 조건을 반환하며 없으면 null을 반환
     */
    private BooleanExpression nameEq(String name) {
        log.debug("🚨 name param: '" + name + "'");
        if (name != null) {
            log.debug("📌 name type: {}", name.getClass().getName()); // 전체 패키지 포함 타입 출력
        } else {
            log.debug("📌 name is null");
        }
        // hasText는 null 또는 빈 문자열이 아닌 경우 true를 반환합니다.
        return hasText(name) ? meetEntity.meetName.likeIgnoreCase("%" + name + "%") : null;
    }

    /**
     * 카테고리로 필터링하는 조건을 동적으로 생성합니다.
     * @param category 모임 카테고리 조건
     * @return BooleanExpression, 카테고리가 있을 경우 조건을 반환하며 없으면 null을 반환
     */
    private BooleanExpression categoryEq(Category category) {
        log.debug("🚨 category param: '" + category + "'");
        if (category != null) {
            log.debug("📌 category type: {}", category.getClass().getName()); // 전체 패키지 포함 타입 출력
        } else {
            log.debug("📌 category is null");
        }
        // 카테고리가 null이 아닐 경우 해당 카테고리로 필터링
        return category != null ? meetEntity.meetCategory.eq(category) : null;
    }


}
