package com.example.prospera.DTO;

public record PushSubscriptionRecord(String endpoint, Long expirationTime, PushSubscriptionKeysRecord keys) {
}
