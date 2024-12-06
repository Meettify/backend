package com.example.meettify.repository.order;

import com.example.meettify.dto.order.PayStatus;
import com.example.meettify.entity.order.OrderEntity;
import com.example.meettify.entity.order.QOrderEntity;
import com.example.meettify.exception.order.OrderException;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.meettify.entity.order.QOrderEntity.orderEntity;

@RequiredArgsConstructor
@Log4j2
public class OrderRepositoryImpl implements CustomOrderRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderEntity> findAllOrders(Pageable page, PayStatus payStatus) {
        try {
            JPAQuery<OrderEntity> contents = queryFactory
                    .selectFrom(orderEntity)
                    .where(payStatusEq(payStatus))
                    .offset(page.getOffset())
                    .limit(page.getPageSize());

            JPAQuery<Long> count = queryFactory
                    .select(orderEntity.count())
                    .from(orderEntity)
                    .where(payStatusEq(payStatus));

            for (Sort.Order order : page.getSort()) {
                PathBuilder pathBuilder = new PathBuilder(
                        orderEntity.getType(),
                        orderEntity.getMetadata()
                );
                PathBuilder sort = pathBuilder.get(order.getProperty());

                contents.orderBy(
                        new OrderSpecifier<>(
                                order.isDescending() ? Order.DESC : Order.ASC,
                                sort != null ? sort : orderEntity.orderId
                        )
                );
            }
            List<OrderEntity> result = contents.fetch();
            return PageableExecutionUtils.getPage(result, page, count::fetchOne);
        } catch (Exception e) {
            throw new OrderException("데이터베이스에서 주문 정보를 가져오는데 실패했습니다.");
        }
    }

    private BooleanExpression payStatusEq(PayStatus payStatus) {
        return payStatus == null ? null : orderEntity.payStatus.eq(payStatus);
    }
}
