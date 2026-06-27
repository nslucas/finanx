package com.example.prospera.Services;

import com.example.prospera.DTO.PushSubscriptionKeysRecord;
import com.example.prospera.DTO.PushSubscriptionRecord;
import com.example.prospera.DTO.PushUnsubscribeRecord;
import com.example.prospera.Entities.PushSubscription;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.PushSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushSubscriptionServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-27T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    @Mock
    private PushSubscriptionRepository subscriptionRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Test
    void upsertReassignsExistingEndpointToAuthenticatedUser() {
        PushSubscription existing = new PushSubscription(10, 1, "endpoint", "old-p256dh", "old-auth",
                null, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user(2));
        when(subscriptionRepository.findByEndpoint("endpoint")).thenReturn(Optional.of(existing));

        service().upsertAuthenticatedUserSubscription(new PushSubscriptionRecord("endpoint", null,
                new PushSubscriptionKeysRecord("new-p256dh", "new-auth")));

        assertEquals(2, existing.getUserId());
        assertEquals("new-p256dh", existing.getP256dh());
        assertEquals("new-auth", existing.getAuth());
        verify(subscriptionRepository).save(existing);
    }

    @Test
    void unsubscribeDeletesOnlyAuthenticatedUsersEndpoint() {
        PushSubscription existing = new PushSubscription(10, 2, "endpoint", "p256dh", "auth",
                null, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user(2));
        when(subscriptionRepository.findByEndpointAndUserId("endpoint", 2)).thenReturn(Optional.of(existing));

        service().unsubscribeAuthenticatedUserSubscription(new PushUnsubscribeRecord("endpoint"));

        verify(subscriptionRepository).delete(existing);
    }

    private PushSubscriptionService service() {
        return new PushSubscriptionService(subscriptionRepository, authenticatedUserService, CLOCK);
    }

    private User user(Integer id) {
        return new User(id, "User", "", BigDecimal.valueOf(1000), "user" + id + "@test.com");
    }
}
