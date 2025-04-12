package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.comment.services.CommentInteractionService;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class CommentInteractionListener {
    private final CommentInteractionService interactionService;

    @EventListener
    @Async
    @TransactionalEventListener
    public void onUserDeleted(UserDeletedEvent event) {
        interactionService.deleteInteractionsByUserId(event.getUserId());
    }
}
