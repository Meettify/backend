package com.example.meettify.entity.coupon;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.event.RequestEventCouponDTO;
import com.example.meettify.dto.event.UpdateEventDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "coupon")
@Builder
public class EventEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private String title;
    private String content;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponEntity coupon;

    // 쿠폰 추가
    public void addCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    // 이벤트 생성
    public static EventEntity create(String title,
                                     String content){
        return EventEntity.builder()
                .title(title)
                .content(content)
                .build();
    }

    // 이벤트 게시글 수정
    public void update(UpdateEventDTO event) {
        this.title = event.getTitle() == null ? this.title : event.getTitle();
        this.content = event.getContent() == null ? this.content : event.getContent();
    }
}
