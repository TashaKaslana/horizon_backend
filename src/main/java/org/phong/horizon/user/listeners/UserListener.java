package org.phong.horizon.user.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.core.utils.HttpRequestUtils;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.user.events.UserCreatedEvent;
import org.phong.horizon.user.events.UserUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Component
@AllArgsConstructor
public class UserListener {
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Welcome to Horizon! Your account has been successfully created.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));
    }

    //why I send event to notification the user has been deleted account? This is nonsense but just keep it
    @EventListener
    public void onUserDeleted(UserCreatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your account has been successfully deleted.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));
    }

    @EventListener
    public void onUserUpdated(UserUpdatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your account has been successfully updated.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.USER_UPDATE,
                        "User updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        HttpRequestUtils.getClientIpAddress(Objects.requireNonNull(HttpRequestUtils.getCurrentHttpRequest())),
                        HttpRequestUtils.getCurrentHttpRequest().getHeader("User-Agent")
                )
        ));
    }
}