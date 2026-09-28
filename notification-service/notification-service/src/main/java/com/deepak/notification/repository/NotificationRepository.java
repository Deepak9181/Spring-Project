package com.deepak.notification.repository;

import com.deepak.notification.entity.Notification;
import com.deepak.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // used to filter notifications by status
    List<Notification> findByStatus(NotificationStatus status);

    // used by retry scheduler — find RETRYING notifications that still have attempts left
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetry);

    // find all notifications sent to a specific recipient
    List<Notification> findByRecipient(String recipient);
}
