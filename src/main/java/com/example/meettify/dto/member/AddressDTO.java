package com.example.meettify.dto.member;

import com.example.meettify.entity.member.AddressEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class AddressDTO {
    @Schema(description = "주소")
    private String memberAddr;
    @Schema(description = "상세 주소")
    private String memberAddrDetail;
    @Schema(description = "우편 번호")
    private String memberZipCode;

    public static AddressDTO changeDTO(AddressEntity address) {
        return AddressDTO.builder()
                .memberAddr(address.getMemberAddr())
                .memberAddrDetail(address.getMemberAddrDetail())
                .memberZipCode(address.getMemberZipCode())
                .build();
    }

    public static AddressDTO addAddress(String memberAddr,
                                        String memberAddrDetail,
                                        String memberZipCode) {
        return AddressDTO.builder()
                .memberAddr(memberAddr)
                .memberAddrDetail(memberAddrDetail)
                .memberZipCode(memberZipCode)
                .build();
    }
}
