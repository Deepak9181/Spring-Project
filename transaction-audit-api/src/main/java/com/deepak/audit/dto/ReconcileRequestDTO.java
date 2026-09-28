package com.deepak.audit.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReconcileRequestDTO {
    private List<TransactionDTO> systemA;
    private List<TransactionDTO> systemB;
}
