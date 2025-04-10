package org.phong.horizon.notification.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.notification.services.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(CreateNotificationEvent event) {
        notificationService.createEventNotification(event.getSenderUserId(), event.getRequest());
    }
}
