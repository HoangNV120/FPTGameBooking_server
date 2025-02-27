package com.server.controller;

import com.server.dto.request.transaction.FindTransactionRequest;
import com.server.dto.request.transaction.TransactionRequest;
import com.server.dto.response.common.PageableObject;
import com.server.dto.response.common.ResponseGlobal;
import com.server.dto.response.transaction.TransactionMinimalResponse;
import com.server.dto.response.transaction.TransactionResponse;
import com.server.dto.response.user.UserResponse;
import com.server.service.QrGenerateService;
import com.server.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final QrGenerateService qrGenerateService;
    private final TransactionService transactionService;

    @PostMapping("/generate-qr")
    public ResponseGlobal<String> generateQr(@Valid @RequestBody TransactionRequest request) throws Exception {
        String imagePath = qrGenerateService.generateQr(request);
        return new ResponseGlobal<>(imagePath);
    }

    @PostMapping("/add-transaction")
    public ResponseGlobal<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Adding transaction: transaction = {}", request.toString());
        return new ResponseGlobal<>(transactionService.addTransaction(request));
    }

    @PostMapping
    public ResponseGlobal<List<TransactionResponse>> viewTransactionByStatus(@RequestParam("status") String status) {
        log.info("viewTransactionByStatus: status = {}", status);
        return new ResponseGlobal<>(transactionService.findByTransactionByStatus(status));
    }

    @PutMapping("/update-transaction")
    public ResponseGlobal<TransactionResponse> updateTransaction(@RequestParam("id") String id,
                                                                 @RequestParam("status") String status) {
        log.info("updateTransaction: id = {}, status = {}", id, status);
        return new ResponseGlobal<>(transactionService.paymentConfirmation(id, status));
    }

    @PostMapping("/search-by-id")
    public ResponseGlobal<PageableObject<TransactionMinimalResponse>> viewTransactionById(@RequestBody FindTransactionRequest request) {
        log.info("findTransaction: request = {}", request.toString());
        return new ResponseGlobal<>(transactionService.searchTransactionById(request));
    }

    @PostMapping("/search")
    public ResponseGlobal<PageableObject<TransactionResponse>> viewTransaction(@RequestBody FindTransactionRequest request) {
        log.info("findTransaction: request = {}", request.toString());
        return new ResponseGlobal<>(transactionService.searchTransaction(request));
    }

    @GetMapping("/exportToExcelTransactions")
    public ResponseEntity<byte[]> exportToExcelTransactions() {
        List<TransactionResponse> transactionResponses = transactionService.getAllTransactions();
        byte[] excelData = transactionService.exportToExcelTransactions(transactionResponses);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("transactions.xls").build());

        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
    }

}
