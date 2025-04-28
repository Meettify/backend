package com.example.meettify.dto.chat;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SharePlaceDTO {
    private String title;
    private String address;
    private double lat;
    private double lng;
    private String mapUrl;
}
