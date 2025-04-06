package org.phong.horizon.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationErrorEnum {
    NOTIFICATION_NOT_FOUND("Notification not found"),
    NOTIFICATION_ACCESS_DENIED("You do not have permission to access this notification"),
    NOTIFICATION_ALREADY_READ("Notification already read"),
    NOTIFICATION_ALREADY_DELETED("Notification already deleted"),
    NOTIFICATION_TYPE_NOT_SUPPORTED("Notification type not supported");

    private final String message;

    NotificationErrorEnum(String message) {
        this.message = message;
    }

}
