package com.example.meettify.dto.order;

import com.example.meettify.dto.member.AddressDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class OrderRequestWrapperDTO {
    @Schema(description = "주문 요청")
    private List<RequestOrderDTO> orders;
}
