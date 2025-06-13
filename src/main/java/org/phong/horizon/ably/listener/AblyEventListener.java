package org.phong.horizon.ably.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.ably.service.AblyService;
import org.phong.horizon.core.services.AuthService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class AblyEventListener {
    private final AblyService ablyService;
    private final AuthService authService;

    @EventListener
    @Async("applicationTaskExecutor")
    @TransactionalEventListener()
    public void handleAblyAppEvent(AblyPublishableEvent event) {
        log.debug("Handling AblyAppEvent for channel: {}, event: {}", event.getChannelName(), event.getEventName());
        String clientIdForAbly = String.valueOf(authService.getUserIdFromContext());

        if (clientIdForAbly != null) {
            log.debug("--- AblyEventProcessor: Found authenticated user ID via AuthService: {}", clientIdForAbly);
        } else {
            log.debug("--- AblyEventProcessor: No authenticated user context found via AuthService. Treating as system/unattributed event.");
        }

        try {
            ablyService.publishMessage(event.getChannelName(), event.getEventName(), event.getPayload(), clientIdForAbly);
            log.debug("Successfully published message to Ably channel: {}, event: {}", event.getChannelName(), event.getEventName());
        } catch (Exception e) {
            log.error("Error publishing message via AblyEventListener for channel: {}, event: {}: {}",
                    event.getChannelName(), event.getEventName(), e.getMessage(), e);
        }
    }
}

