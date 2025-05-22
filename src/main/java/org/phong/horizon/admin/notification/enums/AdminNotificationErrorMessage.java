package org.phong.horizon.admin.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminNotificationErrorMessage {
    NOTIFICATION_NOT_FOUND("Notification not found with id: %s"),
    INVALID_NOTIFICATION_DATA("Invalid notification data provided.");

    private final String message;
}

