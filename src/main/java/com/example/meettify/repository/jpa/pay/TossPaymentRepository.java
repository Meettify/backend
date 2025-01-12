package com.example.meettify.repository.jpa.pay;

import com.example.meettify.entity.pay.PaymentEntity;
import com.example.meettify.entity.pay.TossPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentRepository extends JpaRepository<TossPaymentEntity, Long> {
    TossPaymentEntity findByOrderUid(String orderUid);

}
