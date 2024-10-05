package com.example.meettify.dto.meetBoard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;


/*
 *   worker  : 조영흔
 *   work    : 서비스에 데이터를 보내주는 용도의 클래스
 *             -> 객체지향적인 개발을 하기 위해서 이렇게 하면 유연성이 증가하여 요청 데이터가 변해도
 *                서비스 로직은 변경되지 않는다.
 *   date    : 2024/10/04
 * */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetBoardCommentServiceDTO {
    private Long meetId;
    private Long meetBoardId;
    private Long parentComment;
    private String content;
    private LocalDateTime postDate;

    // meetId와 meetBoardId를 별도로 받아서 주입
    public static MeetBoardCommentServiceDTO makeServiceDTO(Long meetId, Long meetBoardId, RequestMeetBoardCommentDTO request) {
        return MeetBoardCommentServiceDTO.builder()
                .meetId(meetId)
                .meetBoardId(meetBoardId)
                .parentComment(request.getParentComment())
                .content(request.getContent())
                .postDate(request.getPostDate() != null ? request.getPostDate() : LocalDateTime.now())
                .build();
    }
}
