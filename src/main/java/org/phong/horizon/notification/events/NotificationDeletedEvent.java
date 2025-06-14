package org.phong.horizon.notification.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.notification.utils.NotificationChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class NotificationDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID notificationId;
    private final UUID recipientUserId;

    public NotificationDeletedEvent(Object source, UUID notificationId, UUID recipientUserId) {
        super(source);
        this.notificationId = notificationId;
        this.recipientUserId = recipientUserId;
    }

    @Override
    public String getChannelName() {
        return NotificationChannelNames.notification(recipientUserId);
    }

    @Override
    public String getEventName() {
        return "notification.deleted";
    }
}
