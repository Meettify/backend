package com.example.meettify.service.meet;

import com.example.meettify.dto.item.ResponseItemDTO;
import com.example.meettify.dto.meet.*;
import com.example.meettify.dto.meet.category.Category;
import com.example.meettify.dto.meetBoard.MeetBoardSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/*
 *   worker : 조영흔, 유요한
 *   work   : 모임 서비스 인터페이스 구현
 *   date   : 2024/09/24
 * */
public interface MeetService {

    //모임 만들기
    ResponseMeetDTO makeMeet(MeetServiceDTO meet,String email) throws IOException;
    //모임 제거
    String removeMeet(Long meetId, String email);
    boolean checkEditPermission(Long meetId,String email);
    ResponseMeetDTO update(UpdateMeetServiceDTO meetUpdateServiceDTO, List<MultipartFile> newImages) throws IOException;
    boolean isAlreadyMember(Long meetId, String email);
    void applyToJoinMeet(Long meetId, String email);

    Page<MeetSummaryDTO> meetsSearch(MeetSearchCondition condition, Pageable pageable, String email);

    MeetDetailDTO getMeetDetail(Long meetId);

    MeetRole getMeetRole(Long meetId, String email);

    List<MeetBoardSummaryDTO>  getMeetBoardSummaryList(Long meetId);

    List<ResponseMeetMemberDTO> getMeetMemberList(Long meetId);

    MeetRole updateRole(Long meetMemberId, MeetRole meetRole);

    MeetPermissionDTO getPermission(String email, Long meetId);

    Page<MyMeetResponseDTO> getMyMeet(String email, Pageable page);
}
