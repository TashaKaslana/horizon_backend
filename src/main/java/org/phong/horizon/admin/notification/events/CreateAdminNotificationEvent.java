package org.phong.horizon.admin.notification.events;

import lombok.Getter;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateAdminNotificationEvent extends ApplicationEvent {
    private final CreateAdminNotification createAdminNotificationDto;

    public CreateAdminNotificationEvent(Object source, CreateAdminNotification createAdminNotificationDto) {
        super(source);
        this.createAdminNotificationDto = createAdminNotificationDto;
    }
}

