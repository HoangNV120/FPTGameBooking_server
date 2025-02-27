package com.server.dto.response.transaction;

import com.server.dto.response.user.UserResponse;
import com.server.enums.TransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private int point;
    private UserResponse userApprove;
    private TransactionEnum status;
    private String createdDate;
    private String updatedDate;
}
