package com.example.meettify.dto.meet.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

/*
 *   worker  : 조영흔, 유요한
 *   work    : 모임 관련 정보의 카테고리는 나타내는 클래스
 *   date    : 2024/09/20
 * */
@ToString
@Getter
public enum Category {
    @Schema(name = "스포츠")
    SPORTS,

    @Schema(name = "여행 (캠핑 포함)")
    TRAVEL,

    @Schema(name = "음악")
    MUSIC,

    @Schema(name = "예술 (상품으로 미술 물품 판매)")
    ART,

    @Schema(name = "독서 (서적 및 독서 용품, 책갈피, 북라이트 판매)")
    READING,

    @Schema(name = "건강 (건강 관련 상품 판매)")
    HEALTH,

    @Schema(name = "패션/뷰티")
    FASHION_BEAUTY,

    @Schema(name = "반려동물 (애견 용품)")
    PET_LOVERS
}
