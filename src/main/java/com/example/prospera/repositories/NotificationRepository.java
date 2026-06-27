package com.example.prospera.repositories;

import com.example.prospera.Entities.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDescIdDesc(Integer userId, Pageable pageable);

    List<Notification> findByUserIdAndReadAtIsNullOrderByCreatedAtDescIdDesc(Integer userId, Pageable pageable);

    long countByUserIdAndReadAtIsNull(Integer userId);

    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);

    Optional<Notification> findByDedupeKey(String dedupeKey);

    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :readAt WHERE n.userId = :userId AND n.readAt IS NULL")
    int markAllRead(@Param("userId") Integer userId, @Param("readAt") LocalDateTime readAt);

    @Modifying
    long deleteByReadAtIsNotNullAndCreatedAtBefore(LocalDateTime cutoff);
}
