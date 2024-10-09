package com.example.meettify.dto.board;

import com.example.meettify.entity.community.CommunityImgEntity;
import lombok.*;

/*
 *   writer  : 유요한
 *   work    : 프론트에게 response해줄 상품 이미지 클래스
 *   date    : 2024/10/09
 * */
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class ResponseBoardImgDTO {
    private Long boardId;
    // 파일이 업로드될 경로
    // ex) postImages/2024/09/25
    private String uploadImgPath;
    // 업로드할 파일의 이름을 고유한 UUID 기반으로 변경
    // ex) profile.jpg가 123e4567-e89b-12d3-a456-426614174000.jpg로 변경
    private String uploadImgName;
    // 원본 이미지 이름
    private String originalImgName;
    // s3에 올라온 이미지를 조회할 수 있는 경로
    private String uploadImgUrl;

    // 엔티티 -> DTO
    public static ResponseBoardImgDTO changeDTO(CommunityImgEntity image) {
        return ResponseBoardImgDTO.builder()
                .boardId(image.getItemImgId())
                .uploadImgPath(image.getUploadImgPath())
                .uploadImgName(image.getUploadImgName())
                .originalImgName(image.getOriginalImgName())
                .uploadImgUrl(image.getUploadImgUrl())
                .build();
    }
}
