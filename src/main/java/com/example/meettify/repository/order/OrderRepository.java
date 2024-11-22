package com.example.meettify.repository.order;

import com.example.meettify.entity.order.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // @EntityGraph는 필요한 속성을 정의해서 효율적으로 필요한 관계만 로드할 수 있고, 페이징 기능과의 충돌이 없어 더 나은 성능을 제공합니다.
    @EntityGraph(attributePaths = {"orderItems", "orderItems.item", "member"})
    @Query("SELECT o FROM orders o WHERE o.member.memberEmail = :email ORDER BY o.orderId DESC")
    Page<OrderEntity> findAllByMemberEmail(@Param("email") String email, Pageable pageable);


    void deleteByOrderUUIDid(String orderUUIDid);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    @Query("select o from orders o where o.orderUUIDid = :orderUid")
    OrderEntity findByOrderUUIDid(@Param("orderUid") String orderUUIDid);

    long countByMemberMemberEmail(String memberEmail);

}
