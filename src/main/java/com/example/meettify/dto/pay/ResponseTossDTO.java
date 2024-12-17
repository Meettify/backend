package com.example.meettify.dto.pay;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
@Getter
public class ResponseTossDTO {
    private String payType;
    private Long amount;
    private String orderName;
    private String orderId;
    private String customerEmail;
    private String customerName;
    private String successUrl;
    private String failUrl;

    private String failReason;
    private boolean cancelYN;
    private String cancelReason;
    private String createdAt;
}
