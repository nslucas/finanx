package com.example.prospera.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class NotificationCleanupScheduler {
    private final NotificationService notificationService;
    private final Clock clock;
    private final int retentionDays;

    public NotificationCleanupScheduler(NotificationService notificationService,
                                        Clock clock,
                                        @Value("${app.notifications.retention-days:90}") int retentionDays) {
        this.notificationService = notificationService;
        this.clock = clock;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${app.notifications.cleanup-cron:0 30 3 * * SUN}", zone = "America/Sao_Paulo")
    public void cleanupReadNotifications() {
        notificationService.cleanupReadNotificationsOlderThan(LocalDateTime.now(clock).minusDays(retentionDays));
    }
}
