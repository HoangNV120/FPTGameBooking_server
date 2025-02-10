package com.server.service;

import com.server.dto.request.transaction.TransactionRequest;

public interface QrGenerateService {
    String generateQr(TransactionRequest request) throws Exception;
}
