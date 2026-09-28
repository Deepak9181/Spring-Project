package com.deepak.notification.enums;

public enum NotificationStatus {
    PENDING,    // just created, not yet sent
    SENT,       // successfully delivered
    FAILED,     // all retries exhausted
    RETRYING    // currently being retried
}
