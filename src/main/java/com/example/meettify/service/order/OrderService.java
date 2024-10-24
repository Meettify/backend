package com.example.meettify.service.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;

import java.util.List;

public interface OrderService {
    ResponseOrderDTO saveOrder(List<RequestOrderServiceDTO> orders,
                               String email,
                               AddressDTO address);
}
