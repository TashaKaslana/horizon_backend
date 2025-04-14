package org.phong.horizon.notification.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationRespond;
import org.phong.horizon.notification.services.NotificationService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<RestApiResponse<List<NotificationRespond>>> getMyNotifications(Pageable pageable,
                                                                                         NotificationFilterCriteria filters) {
        if (filters.getIsDeleted() == null) {
            filters.setIsDeleted(false);
        }
        Page<NotificationRespond> notifications = notificationService.getMyNotifications(pageable, filters);
        return RestApiResponse.success(notifications);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<RestApiResponse<NotificationRespond>> getNotificationById(@PathVariable UUID notificationId) {
        NotificationRespond notification = notificationService.getNotificationById(notificationId);
        return RestApiResponse.success(notification);
    }

    @DeleteMapping("/{notificationId}/soft")
    public ResponseEntity<RestApiResponse<Void>> softDeleteNotificationById(@PathVariable UUID notificationId) {
        notificationService.softDeleteNotificationById(notificationId);
        return RestApiResponse.noContent();
    }

    @PostMapping("/cleanup")
    public ResponseEntity<RestApiResponse<Void>> cleanupOldSoftDeletedNotifications() {
        notificationService.cleanupOldSoftDeletedNotifications();
        return RestApiResponse.noContent();
    }

    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<RestApiResponse<Void>> markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return RestApiResponse.noContent();
    }
}
