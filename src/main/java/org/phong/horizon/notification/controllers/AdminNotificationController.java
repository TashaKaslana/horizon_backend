package org.phong.horizon.notification.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationRespond;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
@AllArgsConstructor
public class AdminNotificationController {
    private final NotificationService notificationService;

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<Page<NotificationRespond>> getAllNotificationByRecipientId(Pageable pageable,
                                                                                     @PathVariable UUID recipientId,
                                                                                     NotificationFilterCriteria filters) {
        Page<NotificationRespond> notifications = notificationService.getAllNotificationByRecipientId(pageable, recipientId, filters);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> createAdminNotifications(@RequestBody CreateNotificationRequest request) {
        notificationService.createAdminNotifications(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<Void> updateNotificationById(@PathVariable UUID notificationId,
                                                       @RequestBody UpdateNotificationDto request) {
        notificationService.updateNotificationById(notificationId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}/force")
    public ResponseEntity<Void> forceDeleteNotificationById(@PathVariable UUID notificationId) {
        notificationService.forceDeleteNotificationById(notificationId);
        return ResponseEntity.noContent().build();
    }
}