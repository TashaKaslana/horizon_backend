package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.events.CommentMentionCreatedEvent;
import org.phong.horizon.comment.services.CommentMentionService;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.post.services.PostService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class CommentMentionListener {
    private final PostService postService;
    private final CommentMentionService commentMentionService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @TransactionalEventListener
    @Async
    public void onCommentCreated(CommentMentionCreatedEvent event) {
        commentMentionService.createMentions(event.getCommentId());
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentMentionCreated(CommentMentionCreatedEvent event) {
        String postCaption = postService.getPostById(event.getPostId()).caption();

        for (var entry : event.getMapUsernameToUserId().entrySet()) {
            CreateNotificationEvent notificationEvent = new CreateNotificationEvent(
                    this,
                    entry.getValue(),
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
