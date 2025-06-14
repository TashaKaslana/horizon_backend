package org.phong.horizon.notification.events;

import org.phong.horizon.notification.dtos.CreateNotificationRequest;

public interface NotificationPublishableEvent {
    CreateNotificationRequest getNotificationRequest();
}
