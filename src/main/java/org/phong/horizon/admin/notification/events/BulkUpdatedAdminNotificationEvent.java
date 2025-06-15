package org.phong.horizon.admin.notification.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.admin.notification.utils.AdminNotificationChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkUpdatedAdminNotificationEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> notificationIds;
    private final Boolean isRead;

    public BulkUpdatedAdminNotificationEvent(Object source, List<UUID> notificationIds, Boolean isRead) {
        super(source);
        this.notificationIds = notificationIds;
        this.isRead = isRead;
    }

    @Override
    public String getChannelName() {
        return AdminNotificationChannelNames.adminNotifications();
    }

    @Override
    public String getEventName() {
        return "admin.notification.bulk.updated";
    }
}
