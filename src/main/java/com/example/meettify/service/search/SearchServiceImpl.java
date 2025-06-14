package com.example.meettify.service.search;


import com.example.meettify.config.metric.TimeTrace;
import com.example.meettify.dto.board.ResponseCommunityDTO;
import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.MeetSummaryDTO;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.dto.search.RequestSearchLog;
import com.example.meettify.dto.search.SearchCondition;
import com.example.meettify.dto.search.SearchResponseDTO;
import com.example.meettify.dto.search.SearchResultDTO;
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
        SearchResultDTO searchResponse = searchCustomRepository.searchAll(searchCondition);

        // 2. DB에서 가져온 클래스에서 엔티티 추출
        List<MeetEntity> meetEntityList = searchResponse.getMeetResults();
        List<ItemEntity> itemEntities = searchResponse.getItemResults();
        List<CommunityEntity> communityEntities = searchResponse.getCommunityResults();

        List<MeetSummaryDTO> responseMeetSummaryDTOList;
        MemberEntity member = null;
        // 로그인중일 때 처리
        if(email != null) {
            // 사용자 정보를 통해 모임 멤버 ID 목록 조회
            member = memberRepository.findByMemberEmail(email);
            // 모임의 회원들인지 알기 위해 id 조회해옴
            Set<Long> memberMeetIds = (member != null) ?
                    meetMemberRepository.findMeetMemberIdByEmail(email) : Collections.emptySet();
            // 모임 정보와 그 모임에 속한 회원들을 DTO로 List에 담아줌
            responseMeetSummaryDTOList =
                    meetEntityList.stream()
                            .map(meet -> MeetSummaryDTO.changeDTO(meet, memberMeetIds))
                            .toList();
        } else {
            // 로그인이 아닐 때
            responseMeetSummaryDTOList =
                    meetEntityList.stream()
                            .map(meet -> MeetSummaryDTO.changeDTO(meet, null))
                            .toList();
        }
        log.debug("모임 정보 조회 {}", responseMeetSummaryDTOList);


        // 상품 정보 DTO 리스트로 변환
        List<ResponseItemDTO> responseItemDTOList =
                itemEntities.stream()
                        .map(ResponseItemDTO::changeDTO)
                        .toList();
        log.debug("상품 정보 조회 {}", responseItemDTOList);

        // 커뮤니티 정보 DTO 리스트로 변환
        List<ResponseCommunityDTO> responseBoardDTOS =
                communityEntities.stream()
                        .map(ResponseCommunityDTO::changeCommunity)
                        .toList();
        log.debug("커뮤니티 정보 조회 {}", responseMeetSummaryDTOList);

        List<Category> category = new ArrayList<>();
        for (ResponseItemDTO item : responseItemDTOList) {
            category.add(item.getItemCategory());
        }

        // 레디스에 최신 검색 10개 보여주기 위해 저장
        if (member != null) {
            log.debug("레디스 검색 저장 실행");
            // Log the search term
            RequestSearchLog requestSearchLog = RequestSearchLog.builder()
                    .name(searchCondition.getTotalKeyword())
                    .build();
            redisSearchLogService.saveRecentSearchLog(email, requestSearchLog, category);
        }
        return SearchResponseDTO.changeDTO(responseMeetSummaryDTOList, responseItemDTOList, responseBoardDTOS);
    }
}
