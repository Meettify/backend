package com.example.meettify.dto.cart;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestCartServiceDTO {
    private Long itemId;
    private int itemCount;
}
