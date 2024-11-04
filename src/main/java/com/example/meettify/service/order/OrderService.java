package com.example.meettify.service.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;

import java.util.List;

public interface OrderService {
    // 주문하기
    ResponseOrderDTO saveOrder(List<RequestOrderServiceDTO> orders,
                               String email,
                               AddressDTO address);

    // 내 주문 내역 보기

}
