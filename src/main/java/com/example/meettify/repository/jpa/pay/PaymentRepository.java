package com.example.meettify.repository.jpa.pay;

import com.example.meettify.entity.pay.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity findByOrderUid(String orderUid);
}
