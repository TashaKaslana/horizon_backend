package org.phong.horizon.admin.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.phong.horizon.core.services.LocalizationProvider;

@Getter
@RequiredArgsConstructor
public enum AdminNotificationErrorMessage {
    NOTIFICATION_NOT_FOUND("admin.notification.error.not_found"),
    INVALID_NOTIFICATION_DATA("admin.notification.error.invalid_data");

    private final String messageKey;

    public String getMessage(Object... args) {
        return LocalizationProvider.getMessage(this.messageKey, args);
    }
}

