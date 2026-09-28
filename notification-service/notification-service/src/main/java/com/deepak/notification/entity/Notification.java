package com.deepak.notification.entity;

import com.deepak.notification.enums.NotificationStatus;
import com.deepak.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;           // email address
    private String subject;             // email subject
    private String message;             // notification body

    @Enumerated(EnumType.STRING)
    private NotificationType type;      // EMAIL, SMS, PUSH

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;  // PENDING, SENT, FAILED, RETRYING

    private int retryCount;             // how many retries attempted so far
    private String failureReason;       // last error message if failed

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime lastAttemptAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
    }
}
