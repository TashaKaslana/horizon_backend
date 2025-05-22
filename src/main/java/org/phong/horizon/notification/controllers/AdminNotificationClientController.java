package org.phong.horizon.notification.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.dtos.NotificationFilterCriteria;
import org.phong.horizon.notification.dtos.NotificationResponse;
import org.phong.horizon.notification.dtos.UpdateNotificationDto;
import org.phong.horizon.notification.services.NotificationService;
import org.phong.horizon.core.responses.RestApiResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notification-client")
@AllArgsConstructor
public class AdminNotificationClientController {
    private final NotificationService notificationService;

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<RestApiResponse<List<NotificationResponse>>> getAllNotificationByRecipientId(Pageable pageable,
                                                                                                       @PathVariable UUID recipientId,
                                                                                                       @Valid NotificationFilterCriteria filters) {
        Page<NotificationResponse> notifications = notificationService.getAllNotificationByRecipientId(pageable, recipientId, filters);
        return RestApiResponse.success(notifications);
    }

    @PostMapping("/admin")
    public ResponseEntity<RestApiResponse<Void>> createAdminNotifications(@Valid @RequestBody CreateNotificationRequest request) {
        notificationService.createAdminNotifications(request);
        return RestApiResponse.created();
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<RestApiResponse<Void>> updateNotificationById(@PathVariable UUID notificationId,
                                                                         @Valid @RequestBody UpdateNotificationDto request) {
        notificationService.updateNotificationById(notificationId, request);
        return RestApiResponse.noContent();
    }

    @DeleteMapping("/{notificationId}/force")
    public ResponseEntity<RestApiResponse<Void>> forceDeleteNotificationById(@PathVariable UUID notificationId) {
        notificationService.forceDeleteNotificationById(notificationId);
        return RestApiResponse.noContent();
    }
}
