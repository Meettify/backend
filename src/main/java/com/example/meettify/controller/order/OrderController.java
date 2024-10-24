package com.example.meettify.controller.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerDocs{
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Override
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> saveOrder(@RequestBody List<RequestOrderDTO> orders,
                                       @RequestBody AddressDTO address,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            List<RequestOrderServiceDTO> serviceDTOS = orders.stream()
                    .map(order -> modelMapper.map(order, RequestOrderServiceDTO.class))
                    .collect(Collectors.toList());
            ResponseOrderDTO response = orderService.saveOrder(serviceDTOS, email, address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
