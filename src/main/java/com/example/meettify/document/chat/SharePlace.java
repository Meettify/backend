package com.example.meettify.document.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharePlace {
    private String title;
    private String address;
    private double lat;
    private double lng;
    private String mapUrl;
}
