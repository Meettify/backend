package com.example.meettify.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/*
 *   worker : 유요한
 *   work   : 게시글을 만들 때 서비스에게 보내기 용
 *   date   : 2024/10/09
 * */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UpdateServiceDTO {
    private String title;
    private String content;
    private List<Long> remainImgId; // 남길 이미지
}
