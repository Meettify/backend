package com.example.meettify.service.order;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.*;
import com.example.meettify.entity.cart.CartItemEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.entity.order.OrderEntity;
import com.example.meettify.entity.order.OrderItemEntity;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.exception.order.OrderException;
import com.example.meettify.exception.stock.OutOfStockException;
import com.example.meettify.repository.jpa.cart.CartItemRepository;
import com.example.meettify.repository.jpa.item.ItemRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.order.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@Log4j2
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final HttpSession session;

    // 주문 정보를 임시로 보여줄 메서드
    public ResponseOrderDTO createTempOrder(List<RequestOrderServiceDTO> orders, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            if (findMember == null) {
                throw new MemberException("로그인을 해야 주문할 수 있습니다.");
            }

            // 총 금액을 저장할 변수
            int totalPrice = 0;
            // 모든 주문 아이템에 대한 정보를 담을 리스트
            List<ResponseOrderItemDTO> orderItems = new ArrayList<>();

            for (RequestOrderServiceDTO order : orders) {
                int orderCount = order.getItemCount();
                int itemPrice;
                ItemEntity findItem;

                // 1. 장바구니에서 조회 (장바구니에 있는 경우)
                // 주문할 상품으로 장바구니 상품을 조회해봄
                CartItemEntity findCartItem = cartItemRepository.findByItem_ItemId(order.getItemId());
                log.info("상품으로 조회한 장바구니 상품 조회 {} ", findCartItem);


                // 장바구니에 없는 경우
                // 상품을 바로 구매하려는 경우라서 바로 구매
                if (findCartItem != null) {
                    // 장바구니 상품을 가져옴
                    findItem = findCartItem.getItem();
                    // 장바구니의 상품의 가격을 가져옴
                    itemPrice = findCartItem.getItem().getItemPrice();

                    // 주문 개수랑 상품 가격을 곱해서 총액 계산
                    totalPrice += itemPrice * orderCount;

                    if(findItem.getItemCount() < orderCount) {
                        throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 : " + findItem.getItemCount() + ") 주문 수량 : " + orderCount);
                    }

                    if(findCartItem.getCartCount() < orderCount) {
                        throw new OutOfStockException("장바구니 상품의 재고가 부족합니다. (현재 재고 수량 : " + findCartItem.getCartCount() + ") 주문 수량 : " + orderCount);
                    }

                    // 주문 상품 생성
                    ResponseOrderItemDTO responseOrderItem = ResponseOrderItemDTO.createOrder(orderCount, itemPrice, findItem);
                    // 주문 상품 리스트에 넣기
                    orderItems.add(responseOrderItem);
                } else {

                    // 장바구니에 없는 경우 -> 직접 상품 조회 후 바로 구매 처리
                    findItem = itemRepository.findById(order.getItemId())
                            .orElseThrow(() -> new ItemException("해당 상품이 존재하지 않습니다."));
                    itemPrice = findItem.getItemPrice();

                    // 주문 총액 및 재고 처리
                    totalPrice += itemPrice * orderCount;

                    // 주문 항목 생성
                    ResponseOrderItemDTO responseOrderItem = ResponseOrderItemDTO.createOrder(orderCount, itemPrice, findItem);
                    orderItems.add(responseOrderItem);
                }


            }
            String merchantUid = generateMerchantUid(); //주문번호 생성
            // 주문 DTO 생성
            ResponseOrderDTO response = ResponseOrderDTO.createDTO(orderItems, findMember.getAddress(), merchantUid, totalPrice);

            log.info("responseOrderDTO: {}", response);
            session.setAttribute("order_" + merchantUid, response); // 세션에 저장

            return response;
        } catch (Exception e) {
            log.error("주문 처리 중 에러 발생: " + e.getMessage(), e);
            throw new OrderException("주문하는데 실패 했습니다. : " + e.getMessage());
        }
    }

    // 주문 번호 생성 메서드
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = now.format(formatter).replace("-", "");

        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        return formattedDay + uniqueString;
    }

    // 주문하기
    @Override
    public ResponseOrderDTO saveOrder(List<RequestOrderDTO> orders,
                                      String email,
                                      String orderUUid) {
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

            for (RequestOrderDTO order : orders) {
                int orderCount = order.getItemCount();
                int itemPrice;
                ItemEntity findItem;

                // 1. 장바구니에서 조회 (장바구니에 있는 경우)
                // 주문할 상품으로 장바구니 상품을 조회해봄
                CartItemEntity findCartItem = cartItemRepository.findByItem_ItemId(order.getItemId());
                log.info("상품으로 조회한 장바구니 상품 조회 : " + findCartItem);

                // 장바구니에 없는 경우
                // 상품을 바로 구매하려는 경우라서 바로 구매
                if (findCartItem != null && findCartItem.getCartCount() >= orderCount) {
                    // 장바구니 수량이 단일 구매 수량보다 많거나 같을 경우
                    findItem = findCartItem.getItem();
                    itemPrice = findCartItem.getItem().getItemPrice();

                    // 주문 총액 및 재고 처리
                    totalPrice += itemPrice * orderCount;
                    // 주문할 상품 개수만큼 해당 상품의 재고 수량을 감소
                    findItem.minusItemStock(orderCount);
                    // 재고 수량 상품 디비에 저장
                    // 상품과 주문은 양방향 연관관계가 아니기 때문에 직접 처리해서 저장
                    itemRepository.save(findItem);

                    // 주문 상품 생성
                    OrderItemEntity orderItemEntity = OrderItemEntity.saveOrder(
                            findItem,
                            null,
                            order.getItemCount(),
                            findItem.getItemPrice());
                    orderItemEntities.add(orderItemEntity);

                    // 장바구니 수량에서 구매한 수량 차감
                    findCartItem.setCount(findCartItem.getCartCount() - orderCount);
                    if (findCartItem.getCartCount() == 0) {
                        // 수량이 0이면 장바구니에서 삭제
                        cartItemRepository.delete(findCartItem);
                    } else {
                        cartItemRepository.save(findCartItem);
                    }
                } else {
                    // 장바구니에 없는 경우 혹은 장바구니 수량이 부족한 경우 -> 직접 상품 조회 후 바로 구매 처리
                    findItem = itemRepository.findById(order.getItemId())
                            .orElseThrow(() -> new ItemException("해당 상품이 존재하지 않습니다."));
                    itemPrice = findItem.getItemPrice();

                    // 주문 총액 및 재고 처리
                    totalPrice += itemPrice * orderCount;
                    findItem.minusItemStock(orderCount);
                    itemRepository.save(findItem);

                    // 주문 항목 생성
                    OrderItemEntity orderItemEntity = OrderItemEntity.saveOrder(
                            findItem, null, orderCount, itemPrice);
                    orderItemEntities.add(orderItemEntity);
                }
            }

            ResponseOrderDTO orderInfo = (ResponseOrderDTO) session.getAttribute("order_" + orderUUid);

            if (!orderInfo.getOrderUid().equals(orderUUid)) {
                throw new OrderException("주문 정보와 일치하지 않습니다.");
            }

            // 최종 주문 생성 및 총 금액 반영
            OrderEntity orderEntity = OrderEntity.saveOrder(findMember, findMember.getAddress(), totalPrice, orderUUid);
            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                orderItemEntity.setOrder(orderEntity); // 주문과 연결
                orderEntity.getOrderItems().add(orderItemEntity);
            }

            OrderEntity saveOrder = orderRepository.save(orderEntity); // 주문 저장
            // 주문 상태를 결제 상태로 변경
            saveOrder.changePayStatus(PayStatus.PAY_O);
            return ResponseOrderDTO.changeDTO(saveOrder, AddressDTO.changeDTO(findMember.getAddress()), orderUUid);
        } catch (Exception e) {
            log.error("주문 처리 중 에러 발생: " + e.getMessage(), e);
            throw new OrderException("주문하는데 실패 했습니다. : " + e.getMessage());
        }
    }

    // 내 주문 정보 보기
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseOrderDTO> getMyOrders(String email, Pageable pageable) {
        try {
            Page<OrderEntity> findAllOrders = orderRepository.findAllByMemberEmail(email, pageable);
            log.info("findAllOrders{} ", findAllOrders);
            return findAllOrders
                    .map(ResponseOrderDTO::viewChangeDTO);
        } catch (Exception e) {
            log.error("주문 조회 중 에러 발생: " + e.getMessage(), e);
            throw new OrderException("주문 조회 실패 했습니다. : " + e.getMessage());
        }
    }

    // 결제 취소시 주문 취소
    public String cancelOrder(String orderUUID) {
        try {
            // 주문번호로 주문 정보 가져오기
            OrderEntity findOrder = orderRepository.findByOrderUUIDid(orderUUID);
            log.info("findOrder: {}", findOrder);
            findOrder.getOrderItems()
                    .forEach(count -> count.getItem().addItemStock(count.getOrderCount()));
            // 주문 정보에 결제 취소라고 표시
            findOrder.changePayStatus(PayStatus.PAY_X);
            return "주문을 취소했습니다.";
        } catch (Exception e) {
            throw new OrderException("주문 취소하는데 실패했습니다.");
        }
    }

    // 내 주문 카운트
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public long countMyOrders(String email) {
        try {
            long count = orderRepository.countByMemberMemberEmail(email);
            log.info("countMyOrders: {}", count);
            return count;
        } catch (Exception e) {
            throw new OrderException("주문 수량을 가져오는데 실패했습니다.");
        }
    }

    // 모든 주문 수 카운트
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public long countAll() {
        try {
            long count = orderRepository.countAllOrders();
            log.info("countAllOrders: {}", count);
            return count;
        } catch (Exception e) {
            throw new OrderException("주문 수량을 가져오는데 실패했습니다.");
        }
    }

    // 모든 주문 내역 보기
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public Page<ResponseOrderDTO> getOrders(Pageable page, PayStatus payStatus) {
        try {
            Page<OrderEntity> findOrders = orderRepository.findAllOrders(page, payStatus);
            log.info("findOrders: {}", findOrders);
            return findOrders
                    .map(ResponseOrderDTO::viewChangeDTO);
        } catch (Exception e) {
            log.error("주문 조회 중 에러 발생: " + e.getMessage(), e);
            throw new OrderException("주문 조회 실패 했습니다. : " + e.getMessage());
        }
    }
}
