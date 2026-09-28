package com.deepak.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalChecked;
    private int duplicatesFound;
    private int mismatches;

    @Column(length = 1000)
    private String reportSummary;

    private LocalDateTime generatedAt;
}
