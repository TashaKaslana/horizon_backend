package org.phong.horizon.notification.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.utils.ObjectConversion;
import org.phong.horizon.notification.dtos.NotificationResponse;
import org.phong.horizon.notification.utils.NotificationChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class NotificationUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final NotificationResponse notification;

    public NotificationUpdatedEvent(Object source, NotificationResponse notification) {
        super(source);
        this.notification = notification;
    }

    @Override
    public String getChannelName() {
        return NotificationChannelNames.notification(notification.recipientUser().id());
    }

    @Override
    public String getEventName() {
        return "notification.updated";
    }

    @Override
    public Map<String, Object> getPayload() {
        return ObjectConversion.convertObjectToMap(notification);
    }
}
