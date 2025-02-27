package com.server.repository;

import com.server.dto.response.transaction.TransactionResponse;
import com.server.entity.Transaction;
import com.server.enums.TransactionEnum;
import java.io.IOException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByTransactionStatus(TransactionEnum transactionStatus);
}
