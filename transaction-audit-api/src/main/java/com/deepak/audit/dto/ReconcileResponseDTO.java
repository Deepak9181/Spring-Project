package com.deepak.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconcileResponseDTO {
    private int totalInSystemA;
    private int totalInSystemB;
    private int matchedCount;
    private int mismatchCount;
    private int onlyInSystemA;
    private int onlyInSystemB;
    private List<String> mismatches;
    private List<String> missingInSystemB;
    private List<String> missingInSystemA;
}
