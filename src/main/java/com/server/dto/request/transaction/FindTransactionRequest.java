package com.server.dto.request.transaction;

import com.server.dto.request.common.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FindTransactionRequest extends PageableRequest {
    private String transactionId;
    private String userId;
    private String amountOrder;
    private String status;
    private String createdDateOrder;
    private String updatedDateOrder;
}
