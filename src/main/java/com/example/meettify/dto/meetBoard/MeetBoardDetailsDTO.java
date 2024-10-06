package com.example.meettify.dto.meetBoard;


import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.meetBoard.MeetBoardImageEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 *   worker  : 조영흔
 *   work    : 모임 게시판 상세 정보를 표현하기 위한 DTO
 *   date    : 2024/09/26
 * */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetBoardDetailsDTO {
    private Long meetBoardId;
    private String meetBoardTitle;
    private String meetBoardContent;
    private LocalDateTime postDate;
    @Setter
    private List<String> images;
    @Setter
    private List<ResponseMeetBoardCommentDTO> comments;


    public static MeetBoardDetailsDTO fromEntity(MeetBoardEntity meetBoardEntity) {
        // 이미지 URL 리스트 변환
        List<String> imageUrls = meetBoardEntity.getMeetBoardImages() != null ?
                meetBoardEntity.getMeetBoardImages().stream()
                        .map(MeetBoardImageEntity::getUploadFileUrl)
                        .collect(Collectors.toList()) : new ArrayList<>();

        // 댓글 리스트 변환
        List<ResponseMeetBoardCommentDTO> commentDTOs = meetBoardEntity.getComments() != null ?
                meetBoardEntity.getComments().stream()
                        .map(ResponseMeetBoardCommentDTO::changeDTO)
                        .collect(Collectors.toList()) : new ArrayList<>();

        // 빌더 패턴을 사용하여 DTO 생성
        return MeetBoardDetailsDTO.builder()
                .meetBoardId(meetBoardEntity.getMeetBoardId())
                .meetBoardTitle(meetBoardEntity.getMeetBoardTitle())
                .meetBoardContent(meetBoardEntity.getMeetBoardContent())
                .postDate(meetBoardEntity.getPostDate())
                .images(imageUrls)
                .comments(commentDTOs)
                .build();
    }
}

