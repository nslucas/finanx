package com.example.prospera.Services;

import com.example.prospera.Entities.*;
import com.example.prospera.repositories.NotificationRepository;
import com.example.prospera.repositories.PushSubscriptionRepository;
import com.example.prospera.repositories.UserPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-27T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private PushSubscriptionRepository subscriptionRepository;
    @Mock
    private UserPreferenceRepository preferenceRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private PushDeliveryService pushDeliveryService;

    @Test
    void createsInAppNotificationWhenPushPreferenceIsDisabled() {
        UserPreference preference = new UserPreference(null, 1, MovementKind.CARD_EXPENSE, null, null,
                null, null, null, 1);
        preference.setNotifySharedExpenses(false);
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.of(preference));
        when(notificationRepository.findByDedupeKey("key")).thenReturn(Optional.empty());
        when(notificationRepository.saveAndFlush(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(10);
            return notification;
        });

        Notification notification = service().create(1, NotificationType.SHARED_EXPENSE_RECEIVED,
                NotificationCategory.SHARED_EXPENSE, "Title", "Body", "/settlements",
                "EXPENSE_SHARE", 7, "key");

        assertEquals(10, notification.getId());
        verify(notificationRepository).saveAndFlush(any(Notification.class));
        verify(subscriptionRepository, never()).findByUserId(any());
        verify(pushDeliveryService, never()).send(any(), any());
    }

    @Test
    void sendsPushWhenPreferenceIsEnabled() {
        PushSubscription subscription = new PushSubscription(5, 1, "endpoint", "p256dh", "auth",
                null, null, null);
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.empty());
        when(subscriptionRepository.findByUserId(1)).thenReturn(List.of(subscription));
        when(notificationRepository.findByDedupeKey("key")).thenReturn(Optional.empty());
        when(notificationRepository.saveAndFlush(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(10);
            return notification;
        });

        service().create(1, NotificationType.CONNECTION_REQUEST_RECEIVED,
                NotificationCategory.CONNECTION_REQUEST, "Title", "Body", "/connections",
                "CONNECTION_REQUEST", 7, "key");

        verify(pushDeliveryService).send(any(Notification.class), eq(subscription));
    }

    private NotificationService service() {
        return new NotificationService(notificationRepository, subscriptionRepository, preferenceRepository,
                authenticatedUserService, pushDeliveryService, CLOCK);
    }

    private User user() {
        return new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
    }
}
