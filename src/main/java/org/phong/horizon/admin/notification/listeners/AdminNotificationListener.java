package org.phong.horizon.admin.notification.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.admin.notification.events.CreateAdminNotificationEvent;
import org.phong.horizon.admin.notification.infrastructure.dtos.AdminNotificationDto;
import org.phong.horizon.admin.notification.services.AdminNotificationService;
import org.phong.horizon.admin.notification.utils.AdminNotificationChannelNames;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationListener {

    private final AdminNotificationService adminNotificationService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // Or another phase if more appropriate
    public void handleCreateAdminNotification(CreateAdminNotificationEvent event) {
        try {
            log.debug("Handling CreateAdminNotificationEvent for relatedId: {}", event.getNotification().relatedId());
            AdminNotificationDto notification = adminNotificationService.createNotification(event.getNotification());

            eventPublisher.publishEvent(new AblyPublishableEvent() {
                @Override
                public String getChannelName() {
                    return AdminNotificationChannelNames.adminNotifications();
                }

                @Override
                public String getEventName() {
                    return "admin.notification.created";
                }

                @Override
                public Map<String, Object> getPayload() {
                    return Map.of(
                        "notificationId", notification.getId(),
                        "notification", event.getNotification()
                    );
                }
            });
            log.debug("Admin notification created successfully for relatedId: {}", event.getNotification().relatedId());
        } catch (Exception e) {
            log.error("Failed to create admin notification for relatedId: {}. Error: {}",
                      event.getNotification().relatedId(), e.getMessage(), e);
        }
    }
}

