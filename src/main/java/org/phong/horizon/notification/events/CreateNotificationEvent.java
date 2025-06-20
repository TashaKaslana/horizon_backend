package org.phong.horizon.notification.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.utils.ObjectConversion;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.utils.NotificationChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class CreateNotificationEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID senderUserId;
    private final CreateNotificationRequest request;

    public CreateNotificationEvent(Object source, UUID senderUserId, CreateNotificationRequest request) {
        super(source);
        this.senderUserId = senderUserId;
        this.request = request;
    }

    @Override
    public String getChannelName() {
        return NotificationChannelNames.notification(request.getRecipientUserId());
    }

    @Override
    public String getEventName() {
        return "notification.created";
    }

    @Override
    public Map<String, Object> getPayload() {
        return ObjectConversion.convertObjectToMap(request);
    }
}
