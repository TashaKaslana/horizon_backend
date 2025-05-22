package org.phong.horizon.admin.notification.exceptions;

import org.phong.horizon.admin.notification.enums.AdminNotificationErrorMessage;

public class AdminNotificationException extends RuntimeException {

    public AdminNotificationException(AdminNotificationErrorMessage errorMessage, Object... args) {
        super(String.format(errorMessage.getMessage(), args));
    }

    public AdminNotificationException(String message) {
        super(message);
    }

    public AdminNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}

