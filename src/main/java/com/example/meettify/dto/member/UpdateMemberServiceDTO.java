package com.example.meettify.dto.member;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class UpdateMemberServiceDTO {
    private String nickName;
    private String originalMemberPw;
    private String updateMemberPw;
    private AddressDTO memberAddr;


    public static UpdateMemberServiceDTO makeServiceDTO(UpdateMemberDTO member) {
        AddressDTO address =
                member.getMemberAddr() != null ? member.getMemberAddr() : new AddressDTO("", "", "");
        return UpdateMemberServiceDTO.builder()
                .nickName(member.getNickName())
                .originalMemberPw(member.getOriginalMemberPw())
                .updateMemberPw(member.getUpdateMemberPw())
                .memberAddr(member.getMemberAddr())
                .build();
    }
}
