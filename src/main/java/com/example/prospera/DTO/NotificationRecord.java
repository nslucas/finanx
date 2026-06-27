package com.example.prospera.DTO;

import com.example.prospera.Entities.Notification;
import com.example.prospera.Entities.NotificationCategory;
import com.example.prospera.Entities.NotificationType;

import java.time.LocalDateTime;

public record NotificationRecord(Integer id, NotificationType type, NotificationCategory category, String title,
                                 String body, String url, String resourceType, Integer resourceId,
                                 LocalDateTime readAt, LocalDateTime createdAt) {
    public NotificationRecord(Notification notification) {
        this(notification.getId(), notification.getType(), notification.getCategory(), notification.getTitle(),
                notification.getBody(), notification.getUrl(), notification.getResourceType(),
                notification.getResourceId(), notification.getReadAt(), notification.getCreatedAt());
    }
}
