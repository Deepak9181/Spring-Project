package com.deepak.audit.controller;

import com.deepak.audit.dto.ReconcileRequestDTO;
import com.deepak.audit.dto.ReconcileResponseDTO;
import com.deepak.audit.entity.AuditReport;
import com.deepak.audit.service.ReconcileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reconcile")
@RequiredArgsConstructor
public class ReconcileController {

    private final ReconcileService reconcileService;

    // POST /api/reconcile — compare systemA vs systemB
    @PostMapping
    public ResponseEntity<ReconcileResponseDTO> reconcile(@RequestBody ReconcileRequestDTO request) {
        return ResponseEntity.ok(reconcileService.reconcile(request));
    }

    // GET /api/reconcile/reports — all past audit reports
    @GetMapping("/reports")
    public ResponseEntity<List<AuditReport>> getReports() {
        return ResponseEntity.ok(reconcileService.getAllReports());
    }
}
