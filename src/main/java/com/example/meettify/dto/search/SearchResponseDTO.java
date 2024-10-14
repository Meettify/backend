package com.example.meettify.dto.search;

import com.example.meettify.dto.board.ResponseBoardDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SearchResponseDTO {
    List<MeetSummaryDTO> meetSummaryDTOList;
    List<ResponseItemDTO> responseItemDTOList;
    List<ResponseBoardDTO> responseBoardList;

    public static SearchResponseDTO changeDTO( List<MeetSummaryDTO> meetSummaryDTO, List<ResponseItemDTO> responseItemDTOList, List<ResponseBoardDTO> responseBoardDTOS){
        return SearchResponseDTO.builder()
                .meetSummaryDTOList(meetSummaryDTO)
                .responseItemDTOList(responseItemDTOList)
                .responseBoardList(responseBoardDTOS)
                .build();
    }

}
