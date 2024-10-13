package com.example.meettify.dto.meet;

import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.meet.MeetImageEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;


@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MyMeetResponseDTO {
    private Long meetId;
    private String meetName;
    private String location;
    private Category category;
    private Long maximum;
    private List<String> imageUrls;

    public static MyMeetResponseDTO changeDTO(MeetEntity meet) {
        return MyMeetResponseDTO.builder()
                .meetId(meet.getMeetId())
                .meetName(meet.getMeetName())
                .location(meet.getMeetLocation())
                .category(meet.getMeetCategory())
                .maximum(meet.getMeetMaximum())
                .imageUrls(meet.getMeetImages().stream()
                        .map(MeetImageEntity::getUploadFileUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
