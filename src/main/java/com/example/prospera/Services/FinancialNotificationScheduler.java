package com.example.prospera.Services;

import com.example.prospera.DTO.AlertRecord;
import com.example.prospera.Entities.AlertSeverity;
import com.example.prospera.Entities.NotificationCategory;
import com.example.prospera.Entities.NotificationType;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
public class FinancialNotificationScheduler {
    private final UserRepository userRepository;
    private final AlertService alertService;
    private final NotificationService notificationService;
    private final Clock clock;

    public FinancialNotificationScheduler(UserRepository userRepository,
                                          AlertService alertService,
                                          NotificationService notificationService,
                                          Clock clock) {
        this.userRepository = userRepository;
        this.alertService = alertService;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Scheduled(cron = "${app.notifications.digest-cron:0 0 9 * * *}", zone = "America/Sao_Paulo")
    public void createDailyDigestNotifications() {
        LocalDate today = LocalDate.now(clock);
        for (User user : userRepository.findAll()) {
            createDailyDigestNotification(user.getId(), today);
        }
    }

    public void createDailyDigestNotification(Integer userId, LocalDate date) {
        List<AlertRecord> alerts = alertService.getAlertsForUser(userId, date.getMonthValue(), date.getYear(),
                date, date.plusDays(7));
        if (alerts.isEmpty()) {
            return;
        }
        long criticalCount = alerts.stream()
                .filter(alert -> alert.severity() == AlertSeverity.CRITICAL)
                .count();
        notificationService.create(userId, NotificationType.FINANCIAL_ALERT_DIGEST,
                NotificationCategory.FINANCIAL_DIGEST, "Resumo financeiro do dia",
                "Você tem " + alerts.size() + " alerta(s), incluindo " + criticalCount + " crítico(s).",
                "/alerts", "ALERT_DIGEST", null,
                "FINANCIAL_ALERT_DIGEST:" + userId + ":" + date);
    }
}
