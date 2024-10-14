package com.example.meettify.dto.meet;

import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.meet.MeetImageEntity;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetSummaryDTO {
    private Long meetId;
    private String meetName;
    private String location;
    private Category category;
    private Long maximum;
    private Long current;
    private List<String> imageUrls;
    private boolean isMember;


    public static MeetSummaryDTO changeDTO(MeetEntity meet, Set<Long> memberMeetIds) {
        long current = 1;
        if(meet.getMeetMember() != null)
             current = meet.getMeetMember().stream().filter(m->m.getMeetRole() ==MeetRole.ADMIN || m.getMeetRole() == MeetRole.MEMBER ).count();

        return MeetSummaryDTO.builder()
                .meetId(meet.getMeetId())
                .meetName(meet.getMeetName())
                .location(meet.getMeetLocation())
                .category(meet.getMeetCategory())
                .maximum(meet.getMeetMaximum())
                .current(current)
                .imageUrls(meet.getMeetImages().stream()
                        .map(MeetImageEntity::getUploadFileUrl)
                        .collect(Collectors.toList()))
                .isMember(memberMeetIds.contains(meet.getMeetId())) // 사용자가 이 모임의 멤버인지 여부
                .build();
    }

}

