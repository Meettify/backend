package com.example.meettify.service.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.PayStatus;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    // 임시 주문정보
    ResponseOrderDTO createTempOrder(List<RequestOrderServiceDTO> orders,
                                     String email);

    // 주문하기
    ResponseOrderDTO saveOrder(List<RequestOrderDTO> orders,
                               String email,
                               String orderUUid);

    // 내 주문 내역 보기
    Page<ResponseOrderDTO> getMyOrders(String email, Pageable pageable);

    // 주문 취소
    String cancelOrder(String orderUUID);

    // 내 주문 수
    long countMyOrders(String email);

    // 모든 주문 수
    long countAll();

    // 모든 주문 내역 보기
    Page<ResponseOrderDTO> getOrders(Pageable page, PayStatus payStatus);
}
