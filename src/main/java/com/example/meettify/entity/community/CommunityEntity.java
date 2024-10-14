package com.example.meettify.entity.community;

import com.example.meettify.config.auditing.entity.BaseEntity;
import com.example.meettify.dto.board.CreateServiceDTO;
import com.example.meettify.dto.board.UpdateServiceDTO;
import com.example.meettify.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity(name = "communities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Builder
public class CommunityEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long communityId;

    @Column(length = 300, nullable = false)
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemImgId asc ")
    @Builder.Default
    private List<CommunityImgEntity> images = new ArrayList<>();

    // 엔티티 생성
    public static CommunityEntity createEntity(CreateServiceDTO board, MemberEntity member) {
        return CommunityEntity.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .member(member)
                .build();
    }

    // 커뮤니티 수정
    public void updateCommunity(UpdateServiceDTO  community, List<CommunityImgEntity> images) {
        this.title = Optional.ofNullable(community.getTitle()).orElse(this.title);
        this.content = Optional.ofNullable(community.getContent()).orElse(this.content);

        if(this.images == null) {
            this.images = new ArrayList<>();
        }

        if(images != null && !images.isEmpty()) {
            for (CommunityImgEntity image : images) {
                if(!this.images.contains(image)) {
                    this.images.add(image);
                }
            }
        }
    }

    public void remainImgId(List<Long> remainImgId) {
        Set<Long> remainImgIdSet = new HashSet<>(remainImgId); // O(1) 조회를 위한 Set 사용
        this.images.removeIf(img -> !remainImgIdSet.contains(img.getItemImgId()));
    }

    public void removeImg() {
        this.images = new ArrayList<>();
    }
}
