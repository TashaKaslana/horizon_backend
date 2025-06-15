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
public class PostDeletedEvent extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    private final UUID postId;
    private final UUID userId;

    public PostDeletedEvent(Object source, UUID postId, UUID userId) {
        super(source);
        this.postId = postId;
        this.userId = userId;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.post(postId);
    }

    @Override
    public String getEventName() {
        return "post.deleted";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(userId)
                .content("Your post has been successfully deleted.")
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
