package org.phong.horizon.post.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.post.events.PostCreatedEvent;
import org.phong.horizon.post.events.PostDeletedEvent;
import org.phong.horizon.post.events.PostUpdatedEvent;
import org.phong.horizon.post.services.PostService;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserRestoreEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@AllArgsConstructor
public class PostListener {
    private final ApplicationEventPublisher eventPublisher;
    private final PostService postService;

    @Async
    @TransactionalEventListener
    @EventListener
    public void onPostUpdated(PostUpdatedEvent event) {

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.POST_UPDATE,
                        "Post updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        event.getUserAgent(),
                        event.getClientIp()
                )
        ));
    }

    @Async
    @TransactionalEventListener
    @EventListener
    public void onPostDeleted(PostDeletedEvent event) {
        // Notification for this event is handled directly by PostDeletedEvent
        // via the NotificationPublishableEvent interface.
    }

    @EventListener
    @TransactionalEventListener
    @Async
    public void onUserDeleted(UserDeletedEvent event) {
        postService.softDeletePostByUserId(event.getUserId());
    }

    @EventListener
    @TransactionalEventListener
    @Async
    public void onUserRestore(UserRestoreEvent event) {
        postService.restorePostByUserId(event.getUserId());
    }

}