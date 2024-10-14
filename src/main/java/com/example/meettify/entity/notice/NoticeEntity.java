package com.example.meettify.entity.notice;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString(exclude = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class NoticeEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;
    @Column(length = 300, nullable = false)
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    // 엔티티 변환
    public static NoticeEntity changeEntity(CreateServiceDTO notice, MemberEntity member) {
        return NoticeEntity.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .member(member)
                .build();
    }

    // 수정
    public void updateNotice(UpdateServiceDTO notice) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
    }
}
