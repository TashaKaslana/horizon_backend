package org.phong.horizon.notification.controllers;

import lombok.AllArgsConstructor;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationResponse;
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
    public ResponseEntity<RestApiResponse<List<NotificationResponse>>> getMyNotifications(Pageable pageable,
                                                                                          NotificationFilterCriteria filters) {
        if (filters.getIsDeleted() == null) {
            filters.setIsDeleted(false);
        }
        Page<NotificationResponse> notifications = notificationService.getMyNotifications(pageable, filters);
        return RestApiResponse.success(notifications);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<RestApiResponse<NotificationResponse>> getNotificationById(@PathVariable UUID notificationId) {
        NotificationResponse notification = notificationService.getNotificationById(notificationId);
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

    @PatchMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<RestApiResponse<Void>> markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return RestApiResponse.noContent();
    }

    @PatchMapping("/mark-all-as-read")
    public ResponseEntity<RestApiResponse<Void>> markAllNotificationsAsRead(@RequestParam(required = false) String type) {
        notificationService.markAllNotificationsAsRead(type);
        return RestApiResponse.noContent();
    }

    @PatchMapping("/{notificationId}/unmark-as-read")
    public ResponseEntity<RestApiResponse<Void>> unmarkNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.unmarkNotificationAsRead(notificationId);
        return RestApiResponse.noContent();
    }

    @PatchMapping("/unmark-all-as-read")
    public ResponseEntity<RestApiResponse<Void>> unmarkAllNotificationsAsRead(@RequestParam(required = false) String type) {
        notificationService.unmarkAllNotificationsAsRead(type);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/dismiss-all")
    public ResponseEntity<RestApiResponse<Void>> dismissAllNotifications(@RequestParam(required = false) String type) {
        notificationService.dismissAll(type);
        return RestApiResponse.noContent();
    }
}
