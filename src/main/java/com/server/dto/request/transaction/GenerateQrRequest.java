package com.server.dto.request.transaction;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerateQrRequest {
    private String bank;
    private String consumerId;
    private String amount;
}
