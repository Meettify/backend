package com.example.meettify.dto.search;

import com.example.meettify.dto.meet.MeetSummaryDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.meet.MeetEntity;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SearchEntityDTO {

    List<MeetEntity> meetEntities;
    List<ItemEntity> itemEntities;
    List<CommunityEntity> communityEntities;

    public static SearchEntityDTO changeDTO(List<MeetEntity> meetEntities,List<ItemEntity> itemEntities,List<CommunityEntity> communityEntities){

        return SearchEntityDTO.builder()
                .meetEntities(meetEntities)
                .itemEntities(itemEntities)
                .communityEntities(communityEntities)
                .build();
    }

}
