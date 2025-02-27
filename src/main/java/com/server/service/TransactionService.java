package com.server.service;

import com.server.dto.request.transaction.FindTransactionRequest;
import com.server.dto.request.transaction.TransactionRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.transaction.TransactionMinimalResponse;
import com.server.dto.response.transaction.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse addTransaction(TransactionRequest request);

    TransactionResponse paymentConfirmation(String id, String status);

    List<TransactionResponse> findByTransactionByStatus(String status);

    PageableObject<TransactionMinimalResponse> searchTransactionById(FindTransactionRequest request);

    PageableObject<TransactionResponse> searchTransaction(FindTransactionRequest request);
}
