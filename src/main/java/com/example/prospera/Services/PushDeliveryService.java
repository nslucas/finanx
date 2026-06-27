package com.example.prospera.Services;

import com.example.prospera.Entities.Notification;
import com.example.prospera.Entities.PushSubscription;
import com.example.prospera.repositories.PushSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@Service
public class PushDeliveryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushDeliveryService.class);

    private final PushSubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;
    private final String publicKey;
    private final String privateKey;
    private final String subject;

    public PushDeliveryService(PushSubscriptionRepository subscriptionRepository,
                               ObjectMapper objectMapper,
                               @Value("${app.push.vapid.public-key:}") String publicKey,
                               @Value("${app.push.vapid.private-key:}") String privateKey,
                               @Value("${app.push.vapid.subject:mailto:admin@appprospera.com.br}") String subject) {
        this.subscriptionRepository = subscriptionRepository;
        this.objectMapper = objectMapper;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.subject = subject;
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public String getPublicKey() {
        if (publicKey == null || publicKey.isBlank()) {
            throw new IllegalStateException("VAPID public key is not configured");
        }
        return publicKey;
    }

    public void send(Notification notification, PushSubscription subscription) {
        if (!isConfigured()) {
            LOGGER.warn("Skipping push delivery because VAPID keys are not configured");
            return;
        }
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", notification.getTitle());
            payload.put("body", notification.getBody());
            payload.put("url", notification.getUrl());
            payload.put("tag", notification.getType() + ":" + notification.getResourceId());
            payload.put("notificationId", notification.getId());

            PushService pushService = new PushService(publicKey, privateKey, subject);
            HttpResponse response = pushService.send(new nl.martijndwars.webpush.Notification(subscription.getEndpoint(),
                    subscription.getP256dh(), subscription.getAuth(),
                    objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8)));
            int status = response.getStatusLine().getStatusCode();
            if (status == 404 || status == 410) {
                subscriptionRepository.delete(subscription);
            } else if (status < 200 || status >= 300) {
                LOGGER.warn("Push delivery failed for user {} endpoint {} with status {}",
                        subscription.getUserId(), subscription.getEndpoint(), status);
            }
        } catch (Exception exception) {
            LOGGER.warn("Push delivery failed for user {} endpoint {}: {}",
                    subscription.getUserId(), subscription.getEndpoint(), exception.getMessage());
        }
    }

    private boolean isConfigured() {
        return publicKey != null && !publicKey.isBlank()
                && privateKey != null && !privateKey.isBlank()
                && subject != null && !subject.isBlank();
    }
}
