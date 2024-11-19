package com.example.meettify.dto.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class TossCancelDTO {
    @Schema(description = "취소할 금액 (Optional - if null, it will cancel the full amount)")
    private Long cancelAmount;  // Optional field, if not provided, full cancellation happens
    @Schema(description = "취소 사유")
    private String cancelReason;  // Cancellation reason

}
