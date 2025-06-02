package com.example.meettify.dto.search;

import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import lombok.*;

import java.util.List;

/*
 *   writer  : 조영훈
 *   work    : 전체 검색시 프론트에게 반환할 DTO
 *   date    : 2024/10/14
 * */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SearchResponseDTO {
    List<MeetSummaryDTO> meetSummaryDTOList;
    List<ResponseItemDTO> responseItemDTOList;
    List<ResponseCommunityDTO> responseBoardList;

    public static SearchResponseDTO changeDTO( List<MeetSummaryDTO> meetSummaryDTO, List<ResponseItemDTO> responseItemDTOList, List<ResponseCommunityDTO> responseCommunityDTOS){
        return SearchResponseDTO.builder()
                .meetSummaryDTOList(meetSummaryDTO)
                .responseItemDTOList(responseItemDTOList)
                .responseBoardList(responseCommunityDTOS)
                .build();
    }
}
