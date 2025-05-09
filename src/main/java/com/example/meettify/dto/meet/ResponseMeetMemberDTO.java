package com.example.meettify.dto.meet;

import com.example.meettify.entity.meet.MeetMemberEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


/*
 *   worker  : 조영흔
 *   work    : 프론트에게 보내줄 response
 *   date    : 2024/10/02
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseMeetMemberDTO {

    private Long meetMemberId;
    private String nickName;
    private MeetRole meetRole;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime joinDate;
    private String email;


    public static ResponseMeetMemberDTO changeDTO(MeetMemberEntity meetMember) {
        return ResponseMeetMemberDTO.builder()
                .meetMemberId(meetMember.getMeetMemberId())
                .nickName(meetMember.getMemberEntity().getNickName())
                .meetRole(meetMember.getMeetRole())
                .joinDate(meetMember.getJoinDate())
                .email(meetMember.getMemberEntity().getMemberEmail())
                .build();
    }

}
