package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.events.CommentCreated;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.services.CommentMentionService;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CommentListener {
    private final CommentMentionService commentMentionService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreated event) {
        commentMentionService.createMentions(event.getId());
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentUpdated(CommentUpdated event) {
        commentMentionService.renewMentions(event.getCommentId());

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.POST_UPDATE,
                        "Comment updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent"),
                        HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest())
                )
        ));
    }
}
