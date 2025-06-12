package org.phong.horizon.ably.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.event.AblyAppEvent;
import org.phong.horizon.ably.service.AblyService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@AllArgsConstructor
public class AblyEventListener {

    private final AblyService ablyService;

    @EventListener
    @Async
    @TransactionalEventListener
    public void handleAblyAppEvent(AblyAppEvent event) {
        log.info("Handling AblyAppEvent for channel: {}, event: {}", event.getChannelName(), event.getEventName());
        try {
            ablyService.publishMessage(event.getChannelName(), event.getEventName(), event.getData());
            log.info("Successfully published message to Ably channel: {}, event: {}", event.getChannelName(), event.getEventName());
        } catch (Exception e) {
            log.error("Error publishing message via AblyEventListener for channel: {}, event: {}: {}",
                    event.getChannelName(), event.getEventName(), e.getMessage(), e);
        }
    }
}

