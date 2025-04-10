package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.events.CommentMentionCreatedEvent;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.post.services.PostService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CommentMentionListener {
    private final PostService postService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void onCommentMentionCreated(CommentMentionCreatedEvent event) {
        String postCaption = postService.getPostById(event.getPostId()).caption();

        for (var entry : event.getMapUsernameToUserId().entrySet()) {
            UUID userId = UUID.fromString(entry.getKey());

            CreateNotificationEvent notificationEvent = new CreateNotificationEvent(
                    this,
                    userId,
                    CreateNotificationRequest.builder()
                            .recipientUserId(entry.getValue())
                            .postId(event.getPostId())
                            .commentId(event.getCommentId())
                            .content(String.format("%s mentioned you in a comment on post: %s", event.getAuthorUsername(), postCaption))
                            .type(NotificationType.MENTION_COMMENT)
                            .build()
            );

            eventPublisher.publishEvent(notificationEvent);
        }
    }
}
