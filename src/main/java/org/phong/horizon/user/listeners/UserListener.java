package org.phong.horizon.user.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.user.events.UserAccountUpdatedEvent;
import org.phong.horizon.user.events.UserCreatedEvent;
import org.phong.horizon.user.events.UserDeletedEvent;
import org.phong.horizon.user.events.UserRestoreEvent;
import org.phong.horizon.user.events.UserInfoUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;


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

    //why do I send event to notification the user has deleted the account? This is nonsense but just keep it
    @EventListener
    @TransactionalEventListener
    @Async
    public void onUserDeleted(UserDeletedEvent event) {
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
    public void onUserInfoUpdated(UserInfoUpdatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your personal information has been successfully updated.")
                        .extraData(Map.of("diffChange", event.getAdditionalInfo()))
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.USER_UPDATE,
                        "User personal info updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        event.getUserAgent(),
                        event.getClientIp()
                )
        ));
    }

    @EventListener
    public void onUserAccountUpdated(UserAccountUpdatedEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your account information has been successfully updated.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .extraData(Map.of("diffChange", event.getAdditionalInfo()))
                        .build()
        ));

        eventPublisher.publishEvent(new CreateHistoryLogEvent(
                this,
                new CreateHistoryActivity(
                        ActivityTypeCode.USER_UPDATE,
                        "User account info updated",
                        Map.of("diffChange", event.getAdditionalInfo()),
                        event.getUserId(),
                        SystemCategory.USER.getName(),
                        event.getUserId(),
                        event.getUserAgent(),
                        event.getClientIp()
                )
        ));
    }

    @EventListener
    @Async
    @TransactionalEventListener
    public void onUserRestoreEvent(UserRestoreEvent event) {
        eventPublisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getUserId(),
                CreateNotificationRequest.builder()
                        .recipientUserId(event.getUserId())
                        .content("Your account has been successfully restore.")
                        .type(NotificationType.SYSTEM_MESSAGE)
                        .build()
        ));
    }
}