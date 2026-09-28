package com.deepak.notification.controller;

import com.deepak.notification.dto.NotificationRequestDTO;
import com.deepak.notification.dto.NotificationResponseDTO;
import com.deepak.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // POST /api/notifications — create and immediately attempt send
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> send(@RequestBody NotificationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createAndSend(dto));
    }

    // GET /api/notifications — get all
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // GET /api/notifications/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    // GET /api/notifications/status/{status}
    // status values: PENDING, SENT, FAILED, RETRYING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(notificationService.getByStatus(status));
    }

    // GET /api/notifications/recipient/{recipient}
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponseDTO>> getByRecipient(@PathVariable String recipient) {
        return ResponseEntity.ok(notificationService.getByRecipient(recipient));
    }

    // DELETE /api/notifications/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }
}
