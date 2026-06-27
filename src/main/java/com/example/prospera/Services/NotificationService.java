package com.example.prospera.Services;

import com.example.prospera.DTO.NotificationRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.NotificationRepository;
import com.example.prospera.repositories.PushSubscriptionRepository;
import com.example.prospera.repositories.UserPreferenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 100;

    private final NotificationRepository notificationRepository;
    private final PushSubscriptionRepository subscriptionRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final PushDeliveryService pushDeliveryService;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               PushSubscriptionRepository subscriptionRepository,
                               UserPreferenceRepository preferenceRepository,
                               AuthenticatedUserService authenticatedUserService,
                               PushDeliveryService pushDeliveryService,
                               Clock clock) {
        this.notificationRepository = notificationRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.preferenceRepository = preferenceRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.pushDeliveryService = pushDeliveryService;
        this.clock = clock;
    }

    public List<NotificationRecord> findAuthenticatedUserNotifications(Integer limit, Boolean unreadOnly) {
        User user = authenticatedUserService.getAuthenticatedUser();
        PageRequest page = PageRequest.of(0, clampLimit(limit));
        List<Notification> notifications = Boolean.TRUE.equals(unreadOnly)
                ? notificationRepository.findByUserIdAndReadAtIsNullOrderByCreatedAtDescIdDesc(user.getId(), page)
                : notificationRepository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), page);
        return notifications.stream().map(NotificationRecord::new).toList();
    }

    public long countAuthenticatedUserUnread() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return notificationRepository.countByUserIdAndReadAtIsNull(user.getId());
    }

    public NotificationRecord markAuthenticatedUserNotificationRead(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Notification not found with id: " + id));
        if (notification.getReadAt() == null) {
            notification.setReadAt(now());
            notification = notificationRepository.save(notification);
        }
        return new NotificationRecord(notification);
    }

    public void markAllAuthenticatedUserNotificationsRead() {
        User user = authenticatedUserService.getAuthenticatedUser();
        notificationRepository.markAllRead(user.getId(), now());
    }

    public Notification create(Integer userId, NotificationType type, NotificationCategory category, String title,
                               String body, String url, String resourceType, Integer resourceId, String dedupeKey) {
        if (dedupeKey != null) {
            Notification existing = notificationRepository.findByDedupeKey(dedupeKey).orElse(null);
            if (existing != null) {
                return existing;
            }
        }

        Notification notification = new Notification(null, userId, type, category, title, body, url,
                resourceType, resourceId, dedupeKey, null, now());
        try {
            notification = notificationRepository.saveAndFlush(notification);
        } catch (DataIntegrityViolationException exception) {
            if (dedupeKey != null) {
                return notificationRepository.findByDedupeKey(dedupeKey).orElseThrow(() -> exception);
            }
            throw exception;
        }
        deliverPushIfEnabled(notification);
        return notification;
    }

    public long cleanupReadNotificationsOlderThan(LocalDateTime cutoff) {
        return notificationRepository.deleteByReadAtIsNotNullAndCreatedAtBefore(cutoff);
    }

    private void deliverPushIfEnabled(Notification notification) {
        if (!isPushEnabled(notification.getUserId(), notification.getCategory())) {
            return;
        }
        subscriptionRepository.findByUserId(notification.getUserId())
                .forEach(subscription -> pushDeliveryService.send(notification, subscription));
    }

    private boolean isPushEnabled(Integer userId, NotificationCategory category) {
        UserPreference preference = preferenceRepository.findByUserId(userId).orElse(null);
        if (preference == null) {
            return true;
        }
        return switch (category) {
            case CONNECTION_REQUEST -> preference.isNotifyConnectionRequests();
            case SHARED_EXPENSE -> preference.isNotifySharedExpenses();
            case FINANCIAL_DIGEST -> preference.isNotifyFinancialDigest();
        };
    }

    private int clampLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
