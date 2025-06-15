package org.phong.horizon.follow.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.follow.events.UserFollowedEvent;
import org.phong.horizon.follow.services.FollowService;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class FollowListener {
    private final ApplicationEventPublisher eventPublisher;
    private final FollowService followService;

    @EventListener
    public void onFollowCreated(UserFollowedEvent event) {
        // Notification for this event is handled by UserFollowedEvent itself.
    }

    @EventListener
    @Async
    @TransactionalEventListener
    public void onUserDelete(UserDeletedEvent event) {
        followService.deleteAllByUserID(event.getUserId());
    }
}
