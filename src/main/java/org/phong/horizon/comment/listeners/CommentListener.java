package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.events.CommentPinned;
import org.phong.horizon.comment.events.CommentUnPinned;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.services.CommentService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserRestoreEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@AllArgsConstructor
public class CommentListener {
    private final ApplicationEventPublisher eventPublisher;
    private final CommentService commentService;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onCommentUpdated(CommentUpdated event) {
        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                        this,
                        new CreateHistoryActivity(
                                ActivityTypeCode.POST_UPDATE,
                                "Comment updated",
                                Map.of("diffChange", event.getAdditionalInfo()),
                                event.getUserId(),
                                SystemCategory.USER.getName(),
                                event.getUserId(),
                                event.getUserAgent(),
                                event.getClientIp()
                        )
                )
        );
    }

    @TransactionalEventListener
    @Async
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {
        commentService.softDeleteCommentsByUserId(event.getUserId());
    }

    @TransactionalEventListener
    @Async
    @EventListener
    public void onUserRestore(UserRestoreEvent event) {
        commentService.restoreCommentsByPostId(event.getUserId());
    }

    // Notification events are now handled directly by the CommentPinned and
    // CommentUnPinned events via the NotificationPublishableEvent interface.
}
