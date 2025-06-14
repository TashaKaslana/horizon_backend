package org.phong.horizon.admin.notification.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.phong.horizon.admin.notification.utils.AdminNotificationChannelNames;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateAdminNotificationEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final CreateAdminNotification createAdminNotificationDto;

    public CreateAdminNotificationEvent(Object source, CreateAdminNotification createAdminNotificationDto) {
        super(source);
        this.createAdminNotificationDto = createAdminNotificationDto;
    }

    @Override
    public String getChannelName() {
        return AdminNotificationChannelNames.adminNotifications();
    }

    @Override
    public String getEventName() {
        return "admin.notification.created";
    }
}

