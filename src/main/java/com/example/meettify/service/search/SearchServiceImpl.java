package com.example.meettify.service.search;


import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchResponseDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.meet.MeetMemberRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.search.SearchCustomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final MemberRepository memberRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final SearchCustomRepositoryImpl searchCustomRepository;


    @TimeTrace
    public SearchResponseDTO searchResponseDTO(SearchCondition searchCondition,String email) {


        HashMap<String, List> searchResponse = searchCustomRepository.searchAll(searchCondition);

        List<MeetEntity> meetEntityList = (List<MeetEntity>)searchResponse.get("meet");
        List<ItemEntity> itemEntities = (List<ItemEntity>)searchResponse.get("item");
        List<CommunityEntity> communityEntities = (List<CommunityEntity>)searchResponse.get("community");


        // 사용자 정보를 통해 모임 멤버 ID 목록 조회
        MemberEntity member = memberRepository.findByMemberEmail(email);
        Set<Long> memberMeetIds = (member != null) ? meetMemberRepository.findIdByEmail(email) : Collections.emptySet();
        List<MeetSummaryDTO> responseMeetSummaryDTOList = meetEntityList.stream().map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds)).toList();
        List<ResponseItemDTO> responseItemDTOList = itemEntities.stream().map(ResponseItemDTO::changeDTO).toList();
        List<ResponseCommunityDTO> responseBoardDTOS = communityEntities.stream().map(ResponseCommunityDTO::changeCommunity).toList();
        return SearchResponseDTO.changeDTO(responseMeetSummaryDTOList, responseItemDTOList, responseBoardDTOS);
    };


}
