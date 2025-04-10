package org.phong.horizon.notification.events;

import lombok.Getter;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class CreateNotificationEvent extends ApplicationEvent {
    private final UUID senderUserId;
    private final CreateNotificationRequest request;

    public CreateNotificationEvent(Object source, UUID senderUserId, CreateNotificationRequest request) {
        super(source);
        this.senderUserId = senderUserId;
        this.request = request;
    }
}
