package com.example.meettify.service.meetBoard;


import com.example.meettify.dto.meetBoard.MeetBoardCommentPermissionDTO;
import com.example.meettify.dto.meetBoard.MeetBoardCommentServiceDTO;
import com.example.meettify.dto.meetBoard.MeetBoardPermissionDTO;
import com.example.meettify.dto.meetBoard.ResponseMeetBoardCommentDTO;

/*
 *   worker : 조영흔
 *   work   : 모임 게시판 댓글 서비스 인터페이스 구현
 *   date   : 2024/10/30
 * */
public interface MeetBoardCommentService {

    ResponseMeetBoardCommentDTO postComment(String email, MeetBoardCommentServiceDTO meetBoardCommentServiceDTO);

    String deleteComment(Long meetBoardCommentId);

    boolean isEditable(String username, Long meetBoardCommentId);

    MeetBoardCommentPermissionDTO getPermission(String email, Long meetBoardCommentId);

}
