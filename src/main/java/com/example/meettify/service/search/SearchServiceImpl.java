package com.example.meettify.service.search;


import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.dto.search.RequestSearchLog;
import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchResponseDTO;
import com.example.meettify.entity.community.CommunityEntity;
import com.example.meettify.entity.item.ItemEntity;
import com.example.meettify.entity.meet.MeetEntity;
import com.example.meettify.entity.member.MemberEntity;
import com.example.meettify.repository.jpa.meet.MeetMemberRepository;
import com.example.meettify.repository.jpa.member.MemberRepository;
import com.example.meettify.repository.jpa.search.SearchCustomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final MemberRepository memberRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final SearchCustomRepositoryImpl searchCustomRepository;
    private final RedisSearchLogService redisSearchLogService;


    @Transactional(readOnly = true)
    @TimeTrace
    public SearchResponseDTO searchResponseDTO(SearchCondition searchCondition, String email) {
        // 1. 검색 결과 조회
        HashMap<String, List> searchResponse = searchCustomRepository.searchAll(searchCondition);

        // 2. 결과를 DTO로 변환
        List<MeetEntity> meetEntityList = (List<MeetEntity>) searchResponse.get("meet");
        List<ItemEntity> itemEntities = (List<ItemEntity>) searchResponse.get("item");
        List<CommunityEntity> communityEntities = (List<CommunityEntity>) searchResponse.get("community");

        // 사용자 정보를 통해 모임 멤버 ID 목록 조회
        MemberEntity member = memberRepository.findByMemberEmail(email);

        // 모임 정보 DTO 리스트로 변환
        Set<Long> memberMeetIds = (member != null) ?
                meetMemberRepository.findIdByEmail(email) : Collections.emptySet();
        List<MeetSummaryDTO> responseMeetSummaryDTOList =
                meetEntityList.stream()
                        .map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds))
                        .toList();

        // 상품 정보 DTO 리스트로 변환
        List<ResponseItemDTO> responseItemDTOList =
                itemEntities.stream()
                        .map(ResponseItemDTO::changeDTO)
                        .toList();

        // 커뮤니티 정보 DTO 리스트로 변환
        List<ResponseCommunityDTO> responseBoardDTOS =
                communityEntities.stream()
                        .map(ResponseCommunityDTO::changeCommunity)
                        .toList();

        List<Category> category = new ArrayList<>();

        for (ResponseItemDTO item : responseItemDTOList) {
            category.add(item.getItemCategory());
        }

        // 레디스에 최신 검색 10개 보여주기 위해 저장
        if (member != null) {
            // Log the search term
            RequestSearchLog requestSearchLog = RequestSearchLog.builder()
                    .name(searchCondition.getTotalKeyword())
                    .build();
            redisSearchLogService.saveRecentSearchLog(email, requestSearchLog, category);
        }
        return SearchResponseDTO.changeDTO(responseMeetSummaryDTOList, responseItemDTOList, responseBoardDTOS);
    }
}
