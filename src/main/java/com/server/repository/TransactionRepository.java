package com.server.repository;

import com.server.entity.Transaction;
import com.server.enums.TransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
    List<Transaction> findByTransactionStatus(TransactionEnum transactionStatus);
}
