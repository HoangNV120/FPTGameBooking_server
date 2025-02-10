package com.server.service;

import com.server.dto.request.transaction.TransactionRequest;
import com.server.dto.response.transaction.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse addTransaction(TransactionRequest request);
    TransactionResponse paymentConfirmation(String id, String status);

    List<TransactionResponse> findByTransactionByStatus(String status);
}
