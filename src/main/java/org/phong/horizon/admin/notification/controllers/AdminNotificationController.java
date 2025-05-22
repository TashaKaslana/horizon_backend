package org.phong.horizon.admin.notification.controllers;

import lombok.RequiredArgsConstructor;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationFilterDto;
import org.phong.horizon.admin.notification.services.AdminNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    public ResponseEntity<Page<AdminNotificationDto>> getAllNotifications(
            Pageable pageable,
            AdminNotificationFilterDto filterDto) {
        return ResponseEntity.ok(adminNotificationService.getAllNotifications(pageable, filterDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminNotificationDto> getNotificationById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminNotificationService.getNotificationById(id));
    }

    @PostMapping
    public ResponseEntity<AdminNotificationDto> createNotification(@RequestBody AdminNotificationDto adminNotificationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminNotificationService.createNotification(adminNotificationDto));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<AdminNotificationDto> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(adminNotificationService.markAsRead(id));
    }

    @PatchMapping("/{id}/unread")
    public ResponseEntity<AdminNotificationDto> markAsUnread(@PathVariable UUID id) {
        return ResponseEntity.ok(adminNotificationService.markAsUnread(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        adminNotificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}

