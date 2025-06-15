package org.phong.horizon.user.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.enums.SystemCategory;
import org.phong.horizon.historyactivity.dtos.CreateHistoryActivity;
import org.phong.horizon.historyactivity.enums.ActivityTypeCode;
import org.phong.horizon.historyactivity.events.CreateHistoryLogEvent;
import org.phong.horizon.user.events.UserAccountUpdatedEvent;
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

    // Notification events are handled by the domain events themselves via
    // the NotificationPublishableEvent interface.

    @EventListener
    public void onUserInfoUpdated(UserInfoUpdatedEvent event) {

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
        // Notification for this event is handled by UserRestoreEvent itself.
    }
}