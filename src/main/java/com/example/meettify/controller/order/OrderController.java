package com.example.meettify.controller.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerDocs{
    private final OrderService orderService;
    private final ModelMapper modelMapper;


    @Override
    @PostMapping("/tempOrder")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createTempOrder(@RequestBody List<RequestOrderDTO> orders,
                                       @RequestBody AddressDTO address,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            List<RequestOrderServiceDTO> serviceDTOS = orders.stream()
                    .map(order -> modelMapper.map(order, RequestOrderServiceDTO.class))
                    .collect(Collectors.toList());
            ResponseOrderDTO response = orderService.createTempOrder(serviceDTOS, email, address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new OrderException(e.getMessage());
        }
    }

    // 내 주문 보기
    @Override
    @GetMapping("/my-order")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getOrders(UserDetails userDetails, Pageable pageable) {
        try {
            String email = userDetails.getUsername();
            Page<ResponseOrderDTO> myOrders = orderService.getMyOrders(email, pageable);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("contents",myOrders.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber",  myOrders.getNumber() + 1);
            // 전체 페이지 수
            response.put("totalPage", myOrders.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", myOrders.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", myOrders.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", myOrders.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", myOrders.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", myOrders.isLast());

            return ResponseEntity.ok().body(response);
        }  catch (Exception e) {
            throw new OrderException(e.getMessage());
        }
    }
}
