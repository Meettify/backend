package com.example.meettify.dto.meet;

import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.entity.meet.MeetEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetDetailDTO {
    private Long meetId;
    private String meetName;
    private String meetDescription;
    private Long meetMaximum;
    private String meetLocation;
    @Builder.Default
    private List<String> images = new ArrayList<>();
    private Category category;



    public static MeetDetailDTO changeDTO(MeetEntity meetEntity) {
        return MeetDetailDTO.builder()
                .meetId(meetEntity.getMeetId())
                .meetName(meetEntity.getMeetName())
                .meetDescription(meetEntity.getMeetDescription())
                .meetMaximum(meetEntity.getMeetMaximum())
                .meetLocation(meetEntity.getMeetLocation())
                .category(meetEntity.getMeetCategory())
                .images(meetEntity.getMeetImages() != null ?
                        meetEntity.getMeetImages().stream().map(e -> e.getUploadFileUrl()).collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}
