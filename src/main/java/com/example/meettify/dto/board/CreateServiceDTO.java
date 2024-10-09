package com.example.meettify.dto.board;

import lombok.*;

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
public class CreateServiceDTO {
    private String title;
    private String content;
}
