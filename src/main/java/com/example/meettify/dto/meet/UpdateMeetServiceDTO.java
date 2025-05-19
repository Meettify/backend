package com.example.meettify.dto.meet;

import com.example.meettify.dto.meet.category.Category;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class UpdateMeetServiceDTO {
    private Long meetId;
    private String meetName;
    private String meetDescription;
    private Long meetMaximum;
    private String meetLocation;
    private List<String> existingImageUrls; // 기존 이미지 URL 리스트 추가
    private Category category;

    public static UpdateMeetServiceDTO makeServiceDTO(Long meetId,UpdateMeetDTO meet) {
        return UpdateMeetServiceDTO.builder()
                .meetId(meetId)
                .meetName(meet.getMeetName())
                .meetDescription(meet.getMeetDescription())
                .meetMaximum(meet.getMeetMaximum())
                .meetLocation(meet.getMeetLocation())
                .existingImageUrls(meet.getExistingImages())
                .category(meet.getCategory())
                .build();
    }
}
