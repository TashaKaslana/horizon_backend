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
public class UserCreatedEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final UUID userId;
    private final String username;
    private final String email;

    public UserCreatedEvent(Object source, UUID userId, String username, String email) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.users();
    }

    @Override
    public String getEventName() {
        return "user.created";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(userId)
                .content("Welcome to Horizon! Your account has been successfully created.")
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
