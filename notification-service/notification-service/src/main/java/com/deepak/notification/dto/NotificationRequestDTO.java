package com.deepak.notification.dto;

import com.deepak.notification.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String recipient;
    private String subject;
    private String message;
    private NotificationType type;
}
