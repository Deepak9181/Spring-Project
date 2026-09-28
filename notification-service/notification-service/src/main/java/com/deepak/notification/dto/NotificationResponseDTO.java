package com.deepak.notification.dto;

import com.deepak.notification.enums.NotificationStatus;
import com.deepak.notification.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String recipient;
    private String subject;
    private String message;
    private NotificationType type;
    private NotificationStatus status;
    private int retryCount;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime lastAttemptAt;
}
