package com.example.meettify.entity.community;

import jakarta.persistence.*;
import lombok.*;

/*
 *   writer  : 유요한
 *   work    : 이미지를 저장해줄 엔티티 클래스
 *   date    : 2024/10/09
 * */
@Entity(name = "community_images")
@Getter
@ToString(exclude = "community")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CommunityImgEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long itemImgId;

    // 파일이 업로드될 경로
    // ex) postImages/2024/09/25
    @Column(name = "upload_img_path")
    private String uploadImgPath;

    // 업로드할 파일의 이름을 고유한 UUID 기반으로 변경
    // ex) profile.jpg가 123e4567-e89b-12d3-a456-426614174000.jpg로 변경
    @Column(name = "upload_img_name")
    private String uploadImgName;

    // 원본 이미지 이름
    @Column(name = "original_img_name")
    private String originalImgName;

    // s3에 올라온 이미지를 조회할 수 있는 경로
    @Column(name = "upload_img_url")
    private String uploadImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private CommunityEntity community;
}
