package com.example.meettify.service.cart;

import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.dto.cart.ResponseCartDTO;
import com.example.meettify.dto.cart.ResponseCartItemDTO;
import com.example.meettify.dto.cart.UpdateCartServiceDTO;
import com.example.meettify.dto.item.status.ItemCartStatus;
import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.cart.CartItemEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.cart.CartException;
import com.example.meettify.exception.item.ItemException;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.cart.CartItemRepository;
import com.example.meettify.repository.cart.CartRepository;
import com.example.meettify.repository.item.ItemRepository;
import com.example.meettify.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 *   writer  : 유요한
 *   work    : 장바구니 서비스를 처리해 줄 클래스
 *   date    : 2024/10/21
 * */
@Transactional
@RequiredArgsConstructor
@Log4j2
@Service
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니에 상품 추가
    @Override
    public ResponseCartDTO addCartItem(RequestCartServiceDTO cart, String email) {
        try {
            log.info("----------------");
            log.info(email);
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            log.info(findMember);
            // 상품 조회
            ItemEntity findItem = itemRepository.findById(cart.getItemId())
                    .orElseThrow(() -> new ItemException("해당 상품이 존재하지 않습니다."));
            log.info("----------------");
            log.info(findItem);

            CartEntity saveCart = null;

            if(findMember == null) {
                throw new MemberException("유저가 존재하지 않습니다.");
            }

            // 장바구니 상품 확인
            CartEntity findCart = cartRepository.findByMemberMemberId(findMember.getMemberId());
            log.info("장바구니 확인 {}" , findCart);

            if(findCart == null) {
                log.info("장바구니가 존재하지 않아서 생성합니다.");
                findCart = CartEntity.saveCart(findMember);
                findCart = cartRepository.save(findCart);
                log.info("----------------");
                log.info(findCart);
            }

            // 장바구니 상품 조회
            CartItemEntity findCartItem = cartItemRepository.findByItem_ItemId(cart.getItemId());
            log.info("----------------");
            log.info(findCartItem);

            if(cart.getItemCount() > findItem.getItemCount()) {
                throw new CartException("장바구니 개수가 " + cart.getItemCount() + "개, 상품 개수 : " + findItem.getItemCount() +"개입니다.");
            }

            if(findCartItem == null) {
                // 기존에 상품이 없으니 새롭게 추가
                findCartItem = CartItemEntity.addCartItem(cart, findCart, findItem);
                findCartItem.changeCartStatus(ItemCartStatus.CART_O); // 사용자별 상태 변경
            } else {
                throw new CartException("기존에 장바구니에 담았습니다.");
            }
            // 장바구니에 장바구니 상품 담기
            findCart.getCartItems().add(findCartItem);
            // 장바구니 총 개수 수정
            findCart.plusCount(findCartItem.getCartCount());
            // 재고 수량 확인
            findItem.checkItemStock(cart.getItemCount());
            // 장바구니에 유저 정보 추가
            findCart.addMember(findMember);
            saveCart = cartRepository.save(findCart);

            return ResponseCartDTO.changeDTO(saveCart, email, findItem);
        } catch (Exception e) {
            throw new CartException("장바구니에 상품을 담는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 장바구니 상품 삭제
    @Override
    public String deleteCartItem(Long cartItemId, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            if (findMember == null) {
                throw new MemberException("해당 유저가 존재하지 않습니다.");
            }

            // 장바구니 조회
            CartEntity findCart = cartRepository.findByMemberMemberId(findMember.getMemberId());
            if (findCart == null || !findCart.getMember().equals(findMember)) {
                throw new MemberException("해당 유저의 장바구니가 아닙니다.");
            }

            // 장바구니 상품 조회
            CartItemEntity findCartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new CartException("장바구니 상품이 존재하지 않습니다."));

            // 장바구니에서 상품 수량 차감
            findCart.minusCount(findCartItem.getCartCount());

            // 장바구니 상품 삭제
            cartItemRepository.deleteById(cartItemId);
            return "장바구니에서 상품을 삭제했습니다.";
        } catch (Exception e) {
            throw new CartException("장바구니 상품을 삭제하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 장바구니 상품 수정
    @Override
    public ResponseCartDTO updateCartItem(Long cartId,
                                          List<UpdateCartServiceDTO> carts,
                                          String email) {
        try {
            // 장바구니 조회
            CartEntity findCart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new CartException("장바구니가 존재하지 않습니다."));
            // 장바구니 상품 조회
            List<CartItemEntity> findAllCartItems = cartItemRepository.findAllByCartCartId(findCart.getCartId());

            // 장바구니 총 개수를 초기화
            int updatedTotalCount = 0;

            // 각 장바구니 상품의 수량을 업데이트
            for (UpdateCartServiceDTO cart : carts) {
                // 장바구니 상품 중 수정할 상품을 찾음
                findAllCartItems.stream()
                        .filter(item -> item.getItem().getItemId().equals(cart.getItemId()))
                        .findFirst()
                        .ifPresent(cartItem -> {
                            // 장바구니 상품의 수량 수정
                            cartItem.updateCart(cartItem.getCartItemId(), cart.getItemCount());
                        });
            }

            // 수정된 장바구니 아이템의 총 개수를 다시 계산
            updatedTotalCount = findAllCartItems.stream()
                    .mapToInt(CartItemEntity::getCartCount)
                    .sum();

            // 장바구니의 총 개수 업데이트
            findCart.updateCount(updatedTotalCount);
            // 장바구니에 장바구니 상품담기
            findCart.getCartItems().addAll(findAllCartItems);

            // 변경된 장바구니 정보를 저장
            CartEntity saveCart = cartRepository.save(findCart);

            // 수정된 장바구니 정보를 반환
            return ResponseCartDTO.changeUpdateDTO(saveCart, email, findAllCartItems);
        } catch (Exception e) {
            throw new CartException("장바구니 상품을 수정하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 장바구니 조회
    @Override
    @Transactional(readOnly = true)
    @TimeTrace
    public ResponseCartDTO cartDetail(Long cartId, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 장바구니 조회
            CartEntity findCart = cartRepository.findByCartId(cartId);
            log.info("findCart = {}", findCart);

            if (findCart.getMember().getMemberId().equals(findMember.getMemberId())) {
                return ResponseCartDTO.changeDetailDTO(findCart);
            }
            throw new MemberException("해당 유저의 장바구니가 아닙니다.");
        } catch (Exception e) {
            throw new CartException("장바구니 조회하는데 실패했습니다. : " + e.getMessage());
        }
    }

    // 장바구니 상품들 조회
    @Override
    public List<ResponseCartItemDTO> getCartItems(String email) {
        try {
            // 장바구니 상품들 조회
            List<CartItemEntity> findCartItems = cartItemRepository.findByCart_MemberMemberEmail(email);
            return findCartItems.stream()
                    .map(ResponseCartItemDTO::changeDetailDTO)
                    .toList();
        } catch (Exception e) {
            throw new CartException("장바구니 상품들을 조회하는 것에 실패했습니다.");
        }
    }
}
