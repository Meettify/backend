package com.example.meettify.entity.community;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "communities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Builder
public class CommunityEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long boardId;

    @Column(length = 300, nullable = false)
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    // 엔티티 생성
    public static CommunityEntity createEntity(CreateServiceDTO board, MemberEntity member) {
        return CommunityEntity.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .member(member)
                .build();
    }
}
