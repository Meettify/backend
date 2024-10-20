package com.example.meettify.service.meetBoard;


import com.example.meettify.dto.meetBoard.*;

/*
 *   worker : 조영흔
 *   work   : 모임 게시판 댓글 서비스 인터페이스 구현
 *   date   : 2024/10/30
 * */
public interface MeetBoardCommentService {

    ResponseMeetBoardCommentDTO postComment(String email, MeetBoardCommentServiceDTO meetBoardCommentServiceDTO);

    String deleteComment(Long meetBoardCommentId);

    MeetBoardCommentPermissionDTO getPermission(String email, Long meetBoardCommentId);

    ResponseMeetBoardCommentDTO updateComment(Long meetBoardCommentId, UpdateMeetBoardCommentDTO updateMeetBoardCommentDTO,String email);
}
