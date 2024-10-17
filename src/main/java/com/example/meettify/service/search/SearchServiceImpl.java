package com.example.meettify.service.search;


import com.example.meettify.dto.board.ResponseCommentDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchEntityDTO;
import com.example.meettify.dto.search.SearchResponseDTO;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.meet.MeetMemberRepository;
import com.example.meettify.repository.member.MemberRepository;
import com.example.meettify.repository.search.SearchCustomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final MemberRepository memberRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final SearchCustomRepositoryImpl searchCustomRepository;



    public SearchResponseDTO searchResponseDTO(SearchCondition searchCondition,String email) {

        SearchEntityDTO searchResponseDTO = searchCustomRepository.searchAll(searchCondition);

        // 사용자 정보를 통해 모임 멤버 ID 목록 조회
        MemberEntity member = memberRepository.findByMemberEmail(email);
        Set<Long> memberMeetIds = (member != null) ? meetMemberRepository.findIdByEmail(email) : Collections.emptySet();
        List<MeetSummaryDTO> responseMeetSummaryDTOList = searchResponseDTO.getMeetEntities().stream().map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds)).toList();
        List<ResponseItemDTO> responseItemDTOList = searchResponseDTO.getItemEntities().stream().map(ResponseItemDTO::changeDTO).toList();
        List<ResponseCommentDTO> responseCommentDTOS = searchResponseDTO.getCommunityEntities().stream().map(ResponseCommentDTO::changeCommunity).toList();
        return SearchResponseDTO.changeDTO(responseMeetSummaryDTOList, responseItemDTOList, responseCommentDTOS);
    };


}
