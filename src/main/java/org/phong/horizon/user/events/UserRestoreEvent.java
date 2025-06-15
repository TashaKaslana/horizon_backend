package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.utils.UserChannelNames;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserRestoreEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final UUID userId;

    public UserRestoreEvent(Object source, UUID id) {
        super(source);
        this.userId = id;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.user(userId);
    }

    @Override
    public String getEventName() {
        return "user.restored";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(userId)
                .content("Your account has been successfully restore.")
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
