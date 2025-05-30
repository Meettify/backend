package com.example.meettify.dto.search;

import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.meet.MeetEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/*
 *   writer  : 유요한
 *   work    :  DB에서 서비스로 반환해줄 DTO
 *              반환 값을 Map으로 담는게 아니라 클래스로 관리하기 위해서 사용
 *   date    : 2025/05/20
 * */
@Getter
@AllArgsConstructor
@Builder
public class SearchResultDTO {
    private final List<MeetEntity> meetResults;
    private final List<ItemEntity> itemResults;
    private final List<CommunityEntity> communityResults;

    public static SearchResultDTO setResult(List<MeetEntity> meetResults,
                                     List<ItemEntity> itemResults,
                                     List<CommunityEntity> communityResults) {
        return SearchResultDTO.builder()
                .meetResults(meetResults)
                .itemResults(itemResults)
                .communityResults(communityResults)
                .build();
    }
}
