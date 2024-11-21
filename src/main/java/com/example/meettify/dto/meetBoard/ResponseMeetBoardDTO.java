package com.example.meettify.dto.meetBoard;


import com.example.meettify.entity.meetBoard.MeetBoardEntity;
import com.example.meettify.entity.meetBoard.MeetBoardImageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseMeetBoardDTO {
    private Long meetBoardId;
    private String meetBoardTitle;
    private String meetBoardContent;
    private List<String> images;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime postDate;


    public static ResponseMeetBoardDTO changeDTO(MeetBoardEntity meetBoard) {
        return ResponseMeetBoardDTO.builder()
                .meetBoardId(meetBoard.getMeetBoardId())
                .meetBoardTitle(meetBoard.getMeetBoardTitle())
                .meetBoardContent(meetBoard.getMeetBoardContent())
                .images(Optional.ofNullable(meetBoard.getMeetBoardImages())
                        .orElse(List.of())  // Return an empty list if meetBoardImages is null
                        .stream()
                        .map(MeetBoardImageEntity::getUploadFileUrl)
                        .collect(Collectors.toList())
                )
                .postDate(meetBoard.getPostDate())
                .build();
    }
}
