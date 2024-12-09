package com.example.meettify.dto.chat;

import com.example.meettify.entity.member.MemberEntity;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class ChatMemberDTO {
    private String nickName;

    public static ChatMemberDTO getMember(MemberEntity member) {
        return ChatMemberDTO.builder()
                .nickName(member.getNickName())
                .build();
    }
}
