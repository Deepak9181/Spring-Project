package com.deepak.audit.repository;

import com.deepak.audit.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTransactionId(String transactionId);

    // finds transactionIds that exist in BOTH systems — need reconciliation
    @Query("SELECT t.transactionId FROM Transaction t " +
            "GROUP BY t.transactionId " +
            "HAVING COUNT(DISTINCT t.source) > 1")
    List<String> findCrossSystemDuplicates();

    List<Transaction> findByStatus(String status);
}