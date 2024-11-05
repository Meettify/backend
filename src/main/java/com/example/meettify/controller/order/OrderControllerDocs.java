package com.example.meettify.controller.order;

import com.example.meettify.dto.cart.RequestCartDTO;
import com.example.meettify.dto.cart.UpdateCartItemDTO;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "주문하기", description = "주문하기 API")
public interface OrderControllerDocs {
    @Operation(summary = "상품 주문하기", description = "상품을 주문하는 API")
    ResponseEntity<?> saveOrder(List<RequestOrderDTO> orders, AddressDTO address, UserDetails userDetails);

    @Operation(summary = "내 주문보기", description = "내 주문을 보는 API")
    ResponseEntity<?> getOrders(UserDetails userDetails, Pageable pageable);


}
