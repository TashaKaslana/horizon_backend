package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.comment.events.CommentCreated;
import org.phong.horizon.comment.events.CommentMentionCreatedEvent;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.services.CommentMentionService;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.phong.horizon.post.services.PostService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Slf4j
public class CommentMentionListener {
    private final PostService postService;
    private final CommentMentionService commentMentionService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @TransactionalEventListener
    @Async
    public void onCommentCreated(CommentCreated event) {
        commentMentionService.createMentions(event.getId(), event.getCurrentUserId());
    }

    @EventListener
    @TransactionalEventListener
    @Async
    public void onCommentUpdated(CommentUpdated event) {
        commentMentionService.renewMentions(event.getCommentId(), event.getCurrentUserId());
    }

    @TransactionalEventListener
    @Async("applicationTaskExecutor")
    public void onCommentMentionCreated(CommentMentionCreatedEvent event) {
        log.debug("Processing CommentMentionCreatedEvent for comment ID: {}, post ID: {}, author: {}",
                event.getCommentId(), event.getPostId(), event.getAuthorUsername());
        String postCaption = postService.getPostById(event.getPostId()).caption();

        for (var entry : event.getMapUsernameToUserId().entrySet()) {
            NotificationPublishableEvent notificationEvent = () -> CreateNotificationRequest.builder()
                    .recipientUserId(entry.getValue())
                    .postId(event.getPostId())
                    .commentId(event.getCommentId())
                    .content(String.format("%s mentioned you in a comment on post: %s", event.getAuthorUsername(), postCaption))
                    .type(NotificationType.MENTION_COMMENT)
                    .build();

            eventPublisher.publishEvent(notificationEvent);
        }
    }
}
