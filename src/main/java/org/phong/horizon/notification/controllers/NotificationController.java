package org.phong.horizon.notification.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationRespond;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<Page<NotificationRespond>> getMyNotifications(Pageable pageable,
                                                                        NotificationFilterCriteria filters) {
        if (filters.getIsDeleted() == null) {
            filters.setIsDeleted(false);
        }
        Page<NotificationRespond> notifications = notificationService.getMyNotifications(pageable, filters);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationRespond> getNotificationById(@PathVariable UUID notificationId) {
        NotificationRespond notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> updateNotificationById(@PathVariable UUID notificationId,
                                                       @RequestBody UpdateNotificationDto request) {
        notificationService.updateNotificationById(notificationId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}/soft")
    public ResponseEntity<Void> softDeleteNotificationById(@PathVariable UUID notificationId) {
        notificationService.softDeleteNotificationById(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldSoftDeletedNotifications() {
        notificationService.cleanupOldSoftDeletedNotifications();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }
}