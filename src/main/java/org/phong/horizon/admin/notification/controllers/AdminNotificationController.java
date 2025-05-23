package org.phong.horizon.admin.notification.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationFilterDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.phong.horizon.admin.notification.services.AdminNotificationService;
import org.phong.horizon.core.responses.RestApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<RestApiResponse<List<AdminNotificationDto>>> getAllNotifications(
            Pageable pageable,
            AdminNotificationFilterDto filterDto) {
        Page<AdminNotificationDto> notifications = adminNotificationService.getAllNotifications(pageable, filterDto);
        return RestApiResponse.success(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestApiResponse<AdminNotificationDto>> getNotificationById(@PathVariable UUID id) {
        AdminNotificationDto notification = adminNotificationService.getNotificationById(id);
        return RestApiResponse.success(notification);
    }

    @PostMapping
    public ResponseEntity<RestApiResponse<AdminNotificationDto>> createNotification(@RequestBody CreateAdminNotification adminNotificationDto) {
        AdminNotificationDto createdNotification = adminNotificationService.createNotification(adminNotificationDto);
        return RestApiResponse.created(createdNotification);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<RestApiResponse<AdminNotificationDto>> markAsRead(@PathVariable UUID id) {
        AdminNotificationDto notification = adminNotificationService.markAsRead(id);
        return RestApiResponse.success(notification);
    }

    @PatchMapping("/{id}/unread")
    public ResponseEntity<RestApiResponse<AdminNotificationDto>> markAsUnread(@PathVariable UUID id) {
        AdminNotificationDto notification = adminNotificationService.markAsUnread(id);
        return RestApiResponse.success(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestApiResponse<Void>> deleteNotification(@PathVariable UUID id) {
        adminNotificationService.deleteNotification(id);
        return RestApiResponse.noContent();
    }
}

