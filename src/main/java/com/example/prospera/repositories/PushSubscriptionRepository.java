package com.example.prospera.repositories;

import com.example.prospera.Entities.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Integer> {
    Optional<PushSubscription> findByEndpoint(String endpoint);

    Optional<PushSubscription> findByEndpointAndUserId(String endpoint, Integer userId);

    List<PushSubscription> findByUserId(Integer userId);
}
