package com.example.prospera.Services;

import com.example.prospera.DTO.PushSubscriptionRecord;
import com.example.prospera.DTO.PushUnsubscribeRecord;
import com.example.prospera.Entities.PushSubscription;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.PushSubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Transactional
public class PushSubscriptionService {
    private final PushSubscriptionRepository subscriptionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final Clock clock;

    public PushSubscriptionService(PushSubscriptionRepository subscriptionRepository,
                                   AuthenticatedUserService authenticatedUserService,
                                   Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.clock = clock;
    }

    public void upsertAuthenticatedUserSubscription(PushSubscriptionRecord record) {
        validate(record);
        User user = authenticatedUserService.getAuthenticatedUser();
        LocalDateTime now = now();
        PushSubscription subscription = subscriptionRepository.findByEndpoint(record.endpoint())
                .orElseGet(() -> new PushSubscription(null, user.getId(), record.endpoint(), null,
                        null, null, now, now));

        subscription.setUserId(user.getId());
        subscription.setEndpoint(record.endpoint());
        subscription.setP256dh(record.keys().p256dh());
        subscription.setAuth(record.keys().auth());
        subscription.setExpirationTime(toLocalDateTime(record.expirationTime()));
        if (subscription.getCreatedAt() == null) {
            subscription.setCreatedAt(now);
        }
        subscription.setUpdatedAt(now);
        subscriptionRepository.save(subscription);
    }

    public void unsubscribeAuthenticatedUserSubscription(PushUnsubscribeRecord record) {
        if (record == null || record.endpoint() == null || record.endpoint().isBlank()) {
            throw new IllegalArgumentException("Endpoint is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        subscriptionRepository.findByEndpointAndUserId(record.endpoint(), user.getId())
                .ifPresent(subscriptionRepository::delete);
    }

    private void validate(PushSubscriptionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Subscription body is required");
        }
        if (record.endpoint() == null || record.endpoint().isBlank()) {
            throw new IllegalArgumentException("Endpoint is required");
        }
        if (record.keys() == null) {
            throw new IllegalArgumentException("Subscription keys are required");
        }
        if (record.keys().p256dh() == null || record.keys().p256dh().isBlank()) {
            throw new IllegalArgumentException("p256dh key is required");
        }
        if (record.keys().auth() == null || record.keys().auth().isBlank()) {
            throw new IllegalArgumentException("auth key is required");
        }
    }

    private LocalDateTime toLocalDateTime(Long expirationTime) {
        if (expirationTime == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(expirationTime), clock.getZone());
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
