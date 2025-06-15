package org.phong.horizon.admin.notification.events;

import lombok.Getter;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateAdminNotificationEvent extends ApplicationEvent {
    private final CreateAdminNotification notification;

    public CreateAdminNotificationEvent(Object source, CreateAdminNotification notification) {
        super(source);
        this.notification = notification;
    }
}

