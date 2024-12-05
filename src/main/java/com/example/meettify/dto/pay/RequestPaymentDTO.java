package com.example.meettify.dto.pay;

import com.example.meettify.dto.member.AddressDTO;
import com.example.meettify.dto.order.RequestOrderDTO;
import com.example.meettify.entity.member.AddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RequestPaymentDTO {
    @Schema(description = "구매 개수")
    private int itemCount;
    @Schema(description = "결제 uid")
    private String impUid;
    @Schema(description = "주문 uid")
    private String orderUid;
    @Schema(description = "결제 방법")
    private String payMethod;
    @Schema(description = "결제 금액")
    private int payPrice;
    @Schema(description = "주문 정보")
    private List<RequestOrderDTO> orders;
    @Schema(description = "우편 번호")
    private String memberAddr;
    @Schema(description = "주소")
    private String memberAddrDetail;
    @Schema(description = "상세 주소")
    private String memberZipCode;

    public static AddressDTO changeDTO(AddressEntity address) {
        return AddressDTO.builder()
                .memberAddr(address.getMemberAddr())
                .memberAddrDetail(address.getMemberAddrDetail())
                .memberZipCode(address.getMemberZipCode())
                .build();
    }
}
