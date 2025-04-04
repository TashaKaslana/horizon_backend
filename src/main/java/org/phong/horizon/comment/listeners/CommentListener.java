package org.phong.horizon.comment.listeners;

import org.phong.horizon.comment.events.CommentCreated;
import org.phong.horizon.comment.events.CommentUpdated;
import org.phong.horizon.comment.services.CommentMentionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CommentListener {
    private final CommentMentionService commentMentionService;

    public CommentListener(CommentMentionService commentMentionService) {
        this.commentMentionService = commentMentionService;
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreated event) {
        commentMentionService.createMentions(event.id());
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentUpdated(CommentUpdated event) {
        commentMentionService.renewMentions(event.comment().getId());
    }
}
