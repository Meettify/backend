package com.example.meettify.config.pay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentFailOutput {
    private String code;      // 오류 코드
    private String message;   // 오류 메시지

    public String code() {
        return code;
    }
}
