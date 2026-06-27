package com.example.prospera.Resources;

import com.example.prospera.DTO.PushSubscriptionRecord;
import com.example.prospera.DTO.PushUnsubscribeRecord;
import com.example.prospera.DTO.VapidPublicKeyRecord;
import com.example.prospera.Services.PushDeliveryService;
import com.example.prospera.Services.PushSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
public class PushResource {
    private final PushDeliveryService pushDeliveryService;
    private final PushSubscriptionService subscriptionService;

    public PushResource(PushDeliveryService pushDeliveryService,
                        PushSubscriptionService subscriptionService) {
        this.pushDeliveryService = pushDeliveryService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/vapid-public-key")
    public ResponseEntity<VapidPublicKeyRecord> publicKey() {
        return ResponseEntity.ok(new VapidPublicKeyRecord(pushDeliveryService.getPublicKey()));
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Void> upsertSubscription(@RequestBody PushSubscriptionRecord record) {
        subscriptionService.upsertAuthenticatedUserSubscription(record);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subscriptions/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody PushUnsubscribeRecord record) {
        subscriptionService.unsubscribeAuthenticatedUserSubscription(record);
        return ResponseEntity.noContent().build();
    }
}
