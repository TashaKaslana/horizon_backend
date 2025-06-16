package org.phong.horizon.ably.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.event.AblyBulkEvent;
import org.phong.horizon.ably.event.AblyChannelMessagesEvent;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.ably.service.AblyService;
import org.phong.horizon.core.factories.GsonFactory;
import org.phong.horizon.core.services.AuthService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.ably.lib.types.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class AblyEventListener {
    private final AblyService ablyService;
    private final AuthService authService;
    private static final Gson gson = GsonFactory.create();

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

    @EventListener
    @Async("applicationTaskExecutor")
    @TransactionalEventListener()
    public void handleAblyBulkEvent(AblyBulkEvent bulkEvent) {
        String clientIdForAbly = String.valueOf(authService.getUserIdFromContext());

        Map<String, List<Message>> channelMessages = new HashMap<>();
        bulkEvent.getChannelEvents().forEach((channel, events) -> {
            List<Message> messages = events.stream().map(ev -> {
                JsonElement messageDataJson = gson.toJsonTree(ev.getPayload());
                Message m = new Message(ev.getEventName(), messageDataJson);
                if (clientIdForAbly != null) {
                    m.clientId = clientIdForAbly;
                }
                return m;
            }).collect(Collectors.toList());
            channelMessages.put(channel, messages);
        });

        try {
            ablyService.publishToMultipleChannels(channelMessages);
            log.debug("Successfully published bulk messages to Ably");
        } catch (Exception e) {
            log.error("Error publishing bulk messages via AblyEventListener: {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Async("applicationTaskExecutor")
    @TransactionalEventListener()
    public void handleAblyChannelMessagesEvent(AblyChannelMessagesEvent event) {
        String clientIdForAbly = String.valueOf(authService.getUserIdFromContext());

        List<Message> messages = event.getEvents().stream().map(ev -> {
            JsonElement messageDataJson = gson.toJsonTree(ev.getPayload());
            Message m = new Message(ev.getEventName(), messageDataJson);
            if (clientIdForAbly != null) {
                m.clientId = clientIdForAbly;
            }
            return m;
        }).collect(Collectors.toList());

        try {
            ablyService.publishMessages(event.getChannelName(), messages);
            log.debug("Successfully published multiple messages to Ably channel: {}", event.getChannelName());
        } catch (Exception e) {
            log.error("Error publishing messages via AblyEventListener for channel {}: {}", event.getChannelName(), e.getMessage(), e);
        }
    }
}

