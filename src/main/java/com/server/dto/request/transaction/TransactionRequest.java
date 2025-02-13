package com.server.dto.request.transaction;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class TransactionRequest {
    private String userId;
    private String bank;
    private String consumerId;
    @NotBlank(message = "Số tiền không để trống")
    private String amount;
}
