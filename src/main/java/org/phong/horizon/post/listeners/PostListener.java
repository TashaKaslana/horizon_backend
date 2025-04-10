package org.phong.horizon.post.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.post.events.PostCreatedEvent;
import org.phong.horizon.post.events.PostDeletedEvent;
import org.phong.horizon.post.events.PostUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class PostListener {
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void onPostCreated(PostCreatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your post has been successfully created.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));
    }

    @EventListener
    public void onPostUpdated(PostUpdatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your post has been successfully updated.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.POST_UPDATE,
                        "Post updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest()).getHeader("User-Agent"),
                        HttpRequestUtils.getClientIpAddress(HttpRequestUtils.getCurrentHttpRequest())
                )
        ));
    }

    @EventListener
    public void onPostDeleted(PostDeletedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your post has been successfully deleted.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));
    }
}