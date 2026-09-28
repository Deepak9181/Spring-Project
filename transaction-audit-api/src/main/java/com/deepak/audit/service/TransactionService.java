package com.deepak.audit.service;

import com.deepak.audit.dto.TransactionDTO;
import com.deepak.audit.entity.Transaction;
import com.deepak.audit.exception.ResourceNotFoundException;
import com.deepak.audit.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction save(TransactionDTO dto) {
        Transaction t = new Transaction();
        t.setTransactionId(dto.getTransactionId());
        t.setUserId(dto.getUserId());
        t.setAmount(dto.getAmount());
        t.setStatus(dto.getStatus());
        t.setSource(dto.getSource());
        return transactionRepository.save(t);
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getByTransactionId(String transactionId) {
        List<Transaction> transactions = transactionRepository.findByTransactionId(transactionId);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Transaction not found with transactionId: " + transactionId);
        }
        return transactions;
    }

    public List<String> getDuplicates() {
        return transactionRepository.findCrossSystemDuplicates();
    }

    public List<Transaction> getByStatus(String status) {
        return transactionRepository.findByStatus(status.toUpperCase());
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
}
