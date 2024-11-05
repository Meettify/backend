package com.example.meettify.repository.order;

import com.example.meettify.entity.order.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = "SELECT o FROM orders o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.item i " +
            "JOIN FETCH o.member " +
            "WHERE o.member.memberEmail = :email " +
    "order by o.orderId desc ",
    countQuery = "select count (o) from orders o where o.member.memberEmail = :email")
    Page<OrderEntity> findAllByMemberEmail(@Param("email") String email, Pageable pageable);
}
