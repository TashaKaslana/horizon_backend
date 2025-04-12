package org.phong.horizon.follow.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.follow.events.UserFollowedEvent;
import org.phong.horizon.follow.services.FollowService;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
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
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getFollowerUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getFollowedUserId())
                        .content("You have a new follower: " + event.getFollowerUsername())
                        .type(NotificationType.NEW_FOLLOWER)
                        .build()
        ));
    }

    @EventListener
    @Async
    @TransactionalEventListener
    public void onUserDelete(UserDeletedEvent event) {
        followService.deleteAllByUserID(event.getUserId());
    }
}
