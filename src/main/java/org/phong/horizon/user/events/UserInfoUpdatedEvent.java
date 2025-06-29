package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.phong.horizon.user.utils.UserChannelNames;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class UserInfoUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final UUID userId;
    private final String username;
    private final String email;
    private final Map<String, FieldValueChange> additionalInfo;
    private final String userAgent;
    private final String clientIp;

    public UserInfoUpdatedEvent(Object source,
                                UUID userId,
                                String username,
                                String email,
                                Map<String, FieldValueChange> additionalInfo, String userAgent, String clientIp) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.additionalInfo = additionalInfo;
        this.userAgent = userAgent;
        this.clientIp = clientIp;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.user(userId);
    }

    @Override
    public String getEventName() {
        return "user.info.updated";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(userId)
                .content("Your personal information has been successfully updated.")
                .extraData(Map.of("diffChange", additionalInfo))
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
