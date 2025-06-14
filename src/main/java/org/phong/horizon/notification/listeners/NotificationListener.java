package org.phong.horizon.notification.listeners;

import lombok.AllArgsConstructor;
import org.phong.horizon.core.services.AuthService;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.phong.horizon.notification.events.NotificationPublishableEvent;
import org.phong.horizon.notification.services.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@AllArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;
    private final AuthService authService;

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(CreateNotificationEvent event) {
        notificationService.createEventNotification(event.getSenderUserId(), event.getRequest());
    }

    @TransactionalEventListener
    @Async("applicationTaskExecutor")
    public void handleNotificationEvent(NotificationPublishableEvent event) {
        UUID senderUserId = authService.getUserIdFromContext();

        notificationService.createEventNotification(senderUserId ,event.getNotificationRequest());
    }
}
