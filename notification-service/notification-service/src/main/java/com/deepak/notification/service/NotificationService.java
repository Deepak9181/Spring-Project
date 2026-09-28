package com.deepak.notification.service;

import com.deepak.notification.dto.NotificationRequestDTO;
import com.deepak.notification.dto.NotificationResponseDTO;
import com.deepak.notification.entity.Notification;
import com.deepak.notification.enums.NotificationStatus;
import com.deepak.notification.enums.NotificationType;
import com.deepak.notification.exception.ResourceNotFoundException;
import com.deepak.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Value("${notification.max.retry}")
    private int maxRetry;

    // ── Create and immediately attempt to send ───────────────────
    public NotificationResponseDTO createAndSend(NotificationRequestDTO dto) {
        Notification notification = new Notification();
        notification.setRecipient(dto.getRecipient());
        notification.setSubject(dto.getSubject());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());

        // save first with PENDING status (@PrePersist sets this)
        Notification saved = notificationRepository.save(notification);

        // attempt to send immediately
        attemptSend(saved);

        // save updated status after attempt
        return mapToDTO(notificationRepository.save(saved));
    }

    // ── Core send logic — used by both createAndSend and retry ───
    public void attemptSend(Notification notification) {
        try {
            notification.setLastAttemptAt(LocalDateTime.now());

            if (notification.getType() == NotificationType.EMAIL) {
                emailService.sendEmail(
                        notification.getRecipient(),
                        notification.getSubject(),
                        notification.getMessage()
                );
            }
            // future: SMS and PUSH handling goes here

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setFailureReason(null);
            System.out.println("✅ Notification SENT to: " + notification.getRecipient());

        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setFailureReason(e.getMessage());

            if (notification.getRetryCount() >= maxRetry) {
                notification.setStatus(NotificationStatus.FAILED);
                System.out.println("❌ Notification FAILED after " + maxRetry
                        + " retries for: " + notification.getRecipient());
            } else {
                notification.setStatus(NotificationStatus.RETRYING);
                System.out.println("🔄 Retry " + notification.getRetryCount()
                        + "/" + maxRetry + " for: " + notification.getRecipient());
            }
        }
    }

    // ── Scheduled retry job — runs every 2 minutes ───────────────
    @Scheduled(fixedRate = 120000)
    public void retryFailedNotifications() {
        List<Notification> retryList = notificationRepository
                .findByStatusAndRetryCountLessThan(NotificationStatus.RETRYING, maxRetry);

        if (retryList.isEmpty()) {
            System.out.println("⏰ Retry job ran — no pending retries.");
            return;
        }

        System.out.println("⏰ Retry job running for " + retryList.size() + " notification(s)...");

        for (Notification notification : retryList) {
            attemptSend(notification);
            notificationRepository.save(notification);
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────
    public List<NotificationResponseDTO> getAll() {
        return notificationRepository.findAll()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public NotificationResponseDTO getById(Long id) {
        return mapToDTO(findById(id));
    }

    public List<NotificationResponseDTO> getByStatus(String status) {
        NotificationStatus notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
        return notificationRepository.findByStatus(notificationStatus)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────────────
    private Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));
    }

    private NotificationResponseDTO mapToDTO(Notification n) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setRecipient(n.getRecipient());
        dto.setSubject(n.getSubject());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setStatus(n.getStatus());
        dto.setRetryCount(n.getRetryCount());
        dto.setFailureReason(n.getFailureReason());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setSentAt(n.getSentAt());
        dto.setLastAttemptAt(n.getLastAttemptAt());
        return dto;
    }
}
