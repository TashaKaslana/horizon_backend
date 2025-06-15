package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.utils.PostChannelNames;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PostCreatedEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final UUID postId;
    private final UUID userId;
    private final String title;
    private final String content;

    public PostCreatedEvent(Object source, UUID postId, UUID userId, String title, String content) {
        super(source);
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.posts();
    }

    @Override
    public String getEventName() {
        return "post.created";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(userId)
                .content("Your post has been successfully created.")
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
