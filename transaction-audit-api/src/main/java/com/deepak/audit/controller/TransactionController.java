package com.deepak.audit.controller;

import com.deepak.audit.dto.TransactionDTO;
import com.deepak.audit.entity.Transaction;
import com.deepak.audit.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /api/transactions — add a transaction
    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody TransactionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.save(dto));
    }

    // GET /api/transactions — get all transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }


    // GET /api/transactions/txnid/{transactionId}
    @GetMapping("/txnid/{transactionId}")
    public ResponseEntity<List<Transaction>> getByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getByTransactionId(transactionId));
    }
    // GET /api/transactions/duplicates
    // returns transactionIds present in both SYSTEM_A and SYSTEM_B
    @GetMapping("/duplicates")
    public ResponseEntity<List<String>> getDuplicates() {
        return ResponseEntity.ok(transactionService.getDuplicates());
    }

    // GET /api/transactions/status/{status} — filter by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(transactionService.getByStatus(status));
    }

    // DELETE /api/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
