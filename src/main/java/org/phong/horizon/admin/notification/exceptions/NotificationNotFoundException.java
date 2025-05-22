package org.phong.horizon.admin.notification.exceptions;

import org.phong.horizon.admin.notification.enums.AdminNotificationErrorMessage;

public class NotificationNotFoundException extends AdminNotificationException {

    public NotificationNotFoundException(String id) {
        super(AdminNotificationErrorMessage.NOTIFICATION_NOT_FOUND, id);
    }
}

