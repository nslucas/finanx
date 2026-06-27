package com.example.prospera.Resources;

import com.example.prospera.DTO.NotificationRecord;
import com.example.prospera.DTO.UnreadCountRecord;
import com.example.prospera.Services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationResource {
    private final NotificationService service;

    public NotificationResource(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotificationRecord>> findNotifications(@RequestParam(required = false) Integer limit,
                                                                      @RequestParam(required = false) Boolean unreadOnly) {
        return ResponseEntity.ok(service.findAuthenticatedUserNotifications(limit, unreadOnly));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountRecord> unreadCount() {
        return ResponseEntity.ok(new UnreadCountRecord(service.countAuthenticatedUserUnread()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationRecord> markRead(@PathVariable Integer id) {
        return ResponseEntity.ok(service.markAuthenticatedUserNotificationRead(id));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        service.markAllAuthenticatedUserNotificationsRead();
        return ResponseEntity.noContent().build();
    }
}
