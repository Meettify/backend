package com.example.meettify.service.order;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderServiceDTO;
import com.example.meettify.dto.order.ResponseOrderDTO;
import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.cart.CartItemEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.order.OrderEntity;
import com.example.meettify.entity.order.OrderItemEntity;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.repository.cart.CartItemRepository;
import com.example.meettify.repository.item.ItemRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.order.OrderItemRepository;
import com.example.meettify.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Log4j2
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    // 주문하기
    @Override
    public ResponseOrderDTO saveOrder(List<RequestOrderServiceDTO> orders, String email, AddressDTO address) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            if (findMember == null) {
                throw new MemberException("로그인을 해야 주문할 수 있습니다.");
            }

            // 총 금액을 저장할 변수
            int totalPrice = 0;
            // 모든 주문 아이템에 대한 정보를 담을 리스트
            List<OrderItemEntity> orderItemEntities = new ArrayList<>();

            for (RequestOrderServiceDTO order : orders) {
                CartItemEntity findCartItem = cartItemRepository.findByItem_ItemId(order.getItemId());
                log.info("상품으로 조회한 장바구니 상품 조회 : " + findCartItem);

                if (findCartItem == null) {
                    // 장바구니에 없는 경우
                    ItemEntity findItem = itemRepository.findById(order.getItemId())
                            .orElseThrow(() -> new ItemException("해당 상품이 존재하지 않습니다."));

                    // 가격 계산
                    int orderPrice = findItem.getItemPrice() * order.getItemCount();
                    totalPrice += orderPrice;

                    // 주문 상품 생성
                    OrderItemEntity orderItemEntity = OrderItemEntity.saveOrder(findItem, null, order.getItemCount(), findItem.getItemPrice());
                    orderItemEntities.add(orderItemEntity);
                } else {
                    // 장바구니에 있는 경우
                    int orderPrice = findCartItem.getItem().getItemPrice() * order.getItemCount();
                    totalPrice += orderPrice;

                    // 주문 상품 생성
                    OrderItemEntity orderItemEntity = OrderItemEntity.saveOrder(findCartItem.getItem(), null, order.getItemCount(), findCartItem.getItem().getItemPrice());
                    orderItemEntities.add(orderItemEntity);
                }
            }

            // 최종 주문 생성 및 총 금액 반영
            OrderEntity orderEntity = OrderEntity.saveOrder(findMember, address, totalPrice);
            for (OrderItemEntity itemEntity : orderItemEntities) {
                itemEntity.setOrder(orderEntity); // 주문과 연결
                orderEntity.getOrderItems().add(itemEntity);
            }

            OrderEntity saveOrder = orderRepository.save(orderEntity); // 주문 저장
            return ResponseOrderDTO.changeDTO(saveOrder, address);

        } catch (Exception e) {
            log.error("주문 처리 중 에러 발생: " + e.getMessage(), e);
            throw new OrderException("주문하는데 실패 했습니다. : " + e.getMessage());
        }
    }
}
