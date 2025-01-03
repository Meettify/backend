package com.example.meettify.controller.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.OrderRequestWrapperDTO;
import com.example.meettify.dto.order.PayStatus;
import com.example.meettify.dto.order.RequestOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "주문하기", description = "주문하기 API")
public interface OrderControllerDocs {

    @Operation(summary = "내 주문보기", description = "내 주문을 보는 API")
    ResponseEntity<?> getMyOrders(UserDetails userDetails, Pageable pageable);

    @Operation(summary = "모든 주문보기", description = "모든 주문을 보는 API")
    ResponseEntity<?> getOrders(Pageable pageable, PayStatus payStatus);

    @Operation(summary = "임시 주문보기", description = "임시로 주문을 보는 API")
    ResponseEntity<?> createTempOrder(OrderRequestWrapperDTO orderRequestWrapperDTO,
                                      UserDetails userDetails);

    @Operation(summary = "내 주문수", description = "내 주문수를 보는 API")
    ResponseEntity<?> countMyOrder(UserDetails userDetails);

    @Operation(summary = "모든 주문수", description = "모든 주문수를 보는 API")
    ResponseEntity<?> countOrder(UserDetails userDetails);
}
