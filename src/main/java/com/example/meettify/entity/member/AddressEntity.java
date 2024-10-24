package com.example.meettify.entity.member;

import com.example.meettify.dto.member.AddressDTO;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Embeddable
public class AddressEntity {
    private String memberAddr;
    private String memberAddrDetail;
    private String memberZipCode;

    public static AddressEntity changeEntity(AddressDTO address) {
        return AddressEntity.builder()
                .memberAddr(address.getMemberAddr())
                .memberAddrDetail(address.getMemberAddrDetail())
                .memberZipCode(address.getMemberZipCode())
                .build();
    }
}
