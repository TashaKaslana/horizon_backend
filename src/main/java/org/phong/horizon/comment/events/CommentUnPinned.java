package org.phong.horizon.comment.events;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentUnPinned extends ApplicationEvent implements AblyPublishableEvent, NotificationPublishableEvent {
    UUID commentId;
    UUID postId;
    UUID unpinnerId;
    UUID unpinnedUserId;

    public CommentUnPinned(Object source, UUID commentId, UUID postId, UUID unpinnerId, UUID unpinnedUserId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.unpinnerId = unpinnerId;
        this.unpinnedUserId = unpinnedUserId;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.commentsUnderPost(postId);
    }

    @Override
    public String getEventName() {
        return "comment.unpinned";
    }

    @Override
    public CreateNotificationRequest getNotificationRequest() {
        return CreateNotificationRequest.builder()
                .recipientUserId(unpinnedUserId)
                .content("Your post has been successfully updated.")
                .type(NotificationType.SYSTEM_MESSAGE)
                .build();
    }
}
