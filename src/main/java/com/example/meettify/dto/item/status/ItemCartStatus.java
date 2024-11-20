package com.example.meettify.dto.item.status;

import io.swagger.v3.oas.annotations.media.Schema;

/*
 *   writer  : 유요한
 *   work    : 상품 장바구니 상태를 나타내는 클래스
 *   date    : 2024/11/20
 * */
public enum ItemCartStatus {
    @Schema(description = "장바구니에 존재")
    CART_O,
    @Schema(description = "장바구니에 없음")
    CART_X
}
