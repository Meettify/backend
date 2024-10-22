package com.example.meettify.dto.cart;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UpdateCartServiceDTO {
    private Long itemId;
    private int itemCount;
}
