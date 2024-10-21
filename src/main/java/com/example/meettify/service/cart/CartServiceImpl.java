package com.example.meettify.service.cart;

import com.example.meettify.dto.cart.RequestCartServiceDTO;
import com.example.meettify.dto.cart.ResponseCartDTO;
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
import org.springframework.transaction.annotation.Transactional;

/*
 *   writer  : 유요한
 *   work    : 장바구니 서비스를 처리해 줄 클래스
 *   date    : 2024/10/21
 * */
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니에 상품 추가
    @Override
    public ResponseCartDTO addCartItem(RequestCartServiceDTO cart, String email) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);
            // 상품 조회
            ItemEntity findItem = itemRepository.findById(cart.getItemId())
                    .orElseThrow(() -> new ItemException("해당 상품이 존재하지 않습니다."));

            CartEntity saveCart = null;

            if(findMember != null) {
                // 장바구니 상품 확인
                CartEntity findCart = cartRepository.findByMemberMemberId(findMember.getMemberId());
                log.info("장바구니 확인 {}" , findCart);
                // 장바구니 상품 조회
                CartItemEntity findCartItem = cartItemRepository.findByCartCartId(findCart.getCartId());
                log.info("장바구니 상품 확인 {}" , findCartItem);

                // 기존에 장바구니에 상품이 있어서 추가
                if(findCartItem != null) {
                    // 기존에 상품이 있으니까 거기에 더해주기
                    findCartItem.addCartPlus(cart);
                } else {
                    // 기존에 상품이 없으니 새롭게 추가
                    findCartItem = CartItemEntity.addCartItem(cart, findCart, findItem);
                }
                // 재고 수량 확인
                findItem.checkItemStock(cart.getItemCount());
                // 장바구니에 상품 추가
                findCart.saveCart(findCartItem, findMember);
                saveCart = cartRepository.save(findCart);

                return ResponseCartDTO.changeDTO(saveCart, email, findItem);
            }
            throw new MemberException("해당 유저가 존재하지 않습니다.");
        } catch (Exception e) {
            throw new CartException("장바구니에 상품을 담는데 실패했습니다. : " + e.getMessage());
        }
    }
}
