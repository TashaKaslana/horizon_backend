package org.phong.horizon.post.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.post.services.PostInteractionService;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class PostInteractionListener {
    private final PostInteractionService interactionService;

    @EventListener
    @TransactionalEventListener
    @Async
    public void onUserDeleted(UserDeletedEvent event) {
        interactionService.deleteInteractionsByUserId(event.getUserId());
    }
}
