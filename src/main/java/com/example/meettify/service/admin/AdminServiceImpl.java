package com.example.meettify.service.admin;

import com.example.meettify.entity.cart.CartEntity;
import com.example.meettify.entity.member.BannedMemberEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.exception.member.MemberException;
import com.example.meettify.repository.cart.CartRepository;
import com.example.meettify.repository.member.BannedMemberRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.notification.CustomNotificationRepository;
import com.example.meettify.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AdminServiceImpl implements AdminService {
    private final MemberRepository memberRepository;
    private final BannedMemberRepository bannedMemberRepository;
    private final CustomNotificationRepository customNotificationRepository;
    private final CartRepository cartRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public String removeMember(Long memberId) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException("유저가 없습니다."));
            // SSE 알림 삭제
            customNotificationRepository.deleteAllEventCacheStartWithId(memberId);
            customNotificationRepository.deleteAllEmitterStartWithId(memberId);
            notificationRepository.deleteByMemberMemberId(memberId);
            // 장바구니 조회
            CartEntity findCart = cartRepository.findByMemberMemberId(memberId);
            // 장바구니 삭제
            cartRepository.deleteById(findCart.getCartId());
            // 추방된 회원에 저장
            bannedMemberRepository.save(BannedMemberEntity.create(findMember.getMemberEmail()));
            // 회원 탈퇴
            memberRepository.delete(findMember);
            return "회원을 추방했습니다. 추방된 회원 이메일 : " + findMember.getMemberEmail();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MemberException("회원을 추방하는데 실패했습니다.");
        }
    }
}
