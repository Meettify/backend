package com.example.meettify.dto.meetBoard;

/*
 *   worker  : 조영흔\
 *   work    : 서비스에 데이터를 보내주는 용도의 클래스
 *             -> 객체지향적인 개발을 하기위해서 이렇게 하면 유연성이 증가하여 요청 데이터가 변해도
 *                서비스 로직은 변경되지 않는다.
 *   date    : 2024/09/29
 * */

import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MeetBoardServiceDTO {

    private Long meetId;
    private String meetBoardTitle;
    private String meetBoardContent;
    @Builder.Default
    private List<MultipartFile> imagesFile = new ArrayList<>();
    private LocalDateTime postDate;


    public static MeetBoardServiceDTO makeServiceDTO(RequestMeetBoardDTO request,List<MultipartFile> images) {

        return MeetBoardServiceDTO.builder()
                .meetId(request.getMeetId())
                .meetBoardTitle(request.getMeetBoardTitle())
                .meetBoardContent(request.getMeetBoardContent())
                .imagesFile(images !=null ? images : new ArrayList<>())
                .postDate(LocalDateTime.now())
                .build();
    }

}
