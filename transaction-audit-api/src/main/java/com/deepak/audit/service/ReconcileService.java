package com.deepak.audit.service;

import com.deepak.audit.dto.ReconcileRequestDTO;
import com.deepak.audit.dto.ReconcileResponseDTO;
import com.deepak.audit.dto.TransactionDTO;
import com.deepak.audit.entity.AuditReport;
import com.deepak.audit.repository.AuditReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReconcileService {

    private final AuditReportRepository auditReportRepository;

    // ── Core reconcile logic ─────────────────────────────────────
    public ReconcileResponseDTO reconcile(ReconcileRequestDTO request) {

        List<TransactionDTO> systemA = request.getSystemA();
        List<TransactionDTO> systemB = request.getSystemB();

        // map by transactionId for O(1) lookup
        Map<String, TransactionDTO> mapA = systemA.stream()
                .collect(Collectors.toMap(TransactionDTO::getTransactionId, t -> t));

        Map<String, TransactionDTO> mapB = systemB.stream()
                .collect(Collectors.toMap(TransactionDTO::getTransactionId, t -> t));

        List<String> mismatches       = new ArrayList<>();
        List<String> missingInSystemB = new ArrayList<>();
        List<String> missingInSystemA = new ArrayList<>();
        int matchedCount = 0;

        // check every txn in A against B
        for (Map.Entry<String, TransactionDTO> entry : mapA.entrySet()) {
            String txnId = entry.getKey();
            TransactionDTO txnA = entry.getValue();

            if (!mapB.containsKey(txnId)) {
                missingInSystemB.add(txnId);
            } else {
                TransactionDTO txnB = mapB.get(txnId);
                boolean amountMatch = Objects.equals(txnA.getAmount(), txnB.getAmount());
                boolean statusMatch = Objects.equals(txnA.getStatus(), txnB.getStatus());

                if (amountMatch && statusMatch) {
                    matchedCount++;
                } else {
                    mismatches.add(txnId + " [amountMatch=" + amountMatch
                            + ", statusMatch=" + statusMatch + "]");
                }
            }
        }

        // check txns in B not present in A
        for (String txnId : mapB.keySet()) {
            if (!mapA.containsKey(txnId)) {
                missingInSystemA.add(txnId);
            }
        }

        ReconcileResponseDTO response = new ReconcileResponseDTO(
                systemA.size(),
                systemB.size(),
                matchedCount,
                mismatches.size(),
                missingInSystemB.size(),
                missingInSystemA.size(),
                mismatches,
                missingInSystemB,
                missingInSystemA
        );

        saveReport(response);
        return response;
    }

    // ── Persist report summary to DB ─────────────────────────────
    private void saveReport(ReconcileResponseDTO resp) {
        AuditReport report = new AuditReport();
        report.setTotalChecked(resp.getTotalInSystemA() + resp.getTotalInSystemB());
        report.setDuplicatesFound(0);
        report.setMismatches(resp.getMismatchCount());
        report.setReportSummary(
                "Matched=" + resp.getMatchedCount()
                + " | MissingInB=" + resp.getOnlyInSystemA()
                + " | MissingInA=" + resp.getOnlyInSystemB()
                + " | Mismatches=" + resp.getMismatchCount()
        );
        report.setGeneratedAt(LocalDateTime.now());
        auditReportRepository.save(report);
    }

    // ── Scheduled daily audit job — runs every day at midnight ───
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledDailyAuditLog() {
        AuditReport report = new AuditReport();
        report.setTotalChecked(0);
        report.setDuplicatesFound(0);
        report.setMismatches(0);
        report.setReportSummary("Scheduled daily audit run at " + LocalDateTime.now());
        report.setGeneratedAt(LocalDateTime.now());
        auditReportRepository.save(report);
        System.out.println("✅ Daily audit job ran at: " + LocalDateTime.now());
    }

    public List<AuditReport> getAllReports() {
        return auditReportRepository.findAllByOrderByGeneratedAtDesc();
    }
}
