package com.deepak.audit.dto;

import lombok.Data;

@Data
public class TransactionDTO {
    private String transactionId;
    private String userId;
    private Double amount;
    private String status;
    private String source;
}
