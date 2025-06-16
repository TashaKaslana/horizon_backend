package org.phong.horizon.admin.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminNotificationErrorMessage {
    NOTIFICATION_NOT_FOUND("admin.notification.error.not_found"),
    INVALID_NOTIFICATION_DATA("admin.notification.error.invalid_data");

    private final String messageKey;

    public String getMessage(Object... args) {
        return org.phong.horizon.core.config.LocalizationProvider.getMessage(this.messageKey, args);
    }
}

