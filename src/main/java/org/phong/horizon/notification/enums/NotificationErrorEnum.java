package org.phong.horizon.notification.enums;

import lombok.Getter;

@Getter
public enum NotificationErrorEnum {
    NOTIFICATION_NOT_FOUND("notification.error.not_found"),
    NOTIFICATION_ACCESS_DENIED("notification.error.access_denied"),
    NOTIFICATION_ALREADY_READ("notification.error.already_read"),
    NOTIFICATION_ALREADY_DELETED("notification.error.already_deleted"),
    NOTIFICATION_TYPE_NOT_SUPPORTED("notification.error.type_not_supported");

    private final String messageKey;

    NotificationErrorEnum(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }

}
