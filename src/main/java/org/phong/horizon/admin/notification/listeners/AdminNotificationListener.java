package org.phong.horizon.admin.notification.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.admin.notification.events.CreateAdminNotificationEvent;
import org.phong.horizon.admin.notification.services.AdminNotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationListener {

    private final AdminNotificationService adminNotificationService;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // Or another phase if more appropriate
    public void handleCreateAdminNotification(CreateAdminNotificationEvent event) {
        try {
            log.debug("Handling CreateAdminNotificationEvent for relatedId: {}", event.getNotification().relatedId());
            adminNotificationService.createNotification(event.getNotification());
            log.debug("Admin notification created successfully for relatedId: {}", event.getNotification().relatedId());
        } catch (Exception e) {
            log.error("Failed to create admin notification for relatedId: {}. Error: {}",
                      event.getNotification().relatedId(), e.getMessage(), e);
        }
    }
}

