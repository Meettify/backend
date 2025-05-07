package com.example.meettify.service.meetBoard;

import com.example.meettify.dto.meetBoard.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 *   worker : 조영흔
 *   work   : 모임 게시판 서비스 인터페이스 구현
 *   date   : 2024/09/26
 * */

public interface MeetBoardService {

    MeetBoardDetailsDTO getDetails(String email,
                                   Long meetBoardId,
                                   HttpServletRequest request,
                                   HttpServletResponse response);

    ResponseMeetBoardDTO postBoard(MeetBoardServiceDTO meetBoardServiceDTO, String email) throws Exception;

    String deleteBoard(Long meetId, Long meetBoardId, String username) throws Exception;

    ResponseMeetBoardDTO updateBoardService(UpdateMeetBoardServiceDTO updateMeetBoardServiceDTO, String username) throws Exception;

    Page<MeetBoardSummaryDTO> getPagedList(Long meetId, Pageable pageable);

    MeetBoardPermissionDTO getPermission(String email, Long meetBoardId);
}
