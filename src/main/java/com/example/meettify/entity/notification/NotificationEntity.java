package com.example.meettify.entity.notification;

import com.example.meettify.config.auditing.entity.BaseTimeEntity;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class NotificationEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private String message;

    private boolean isRead;

    public static NotificationEntity save(MemberEntity member, String message) {
        return NotificationEntity.builder()
                .member(member)
                .message(message)
                .isRead(false)
                .build();
    }

    public void changeRead() {
        this.isRead = true;
    }
}
