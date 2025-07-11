package org.phong.horizon.ably.service;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.CompletionListener;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ErrorInfo;
import io.ably.lib.types.Message;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.exception.AblyPublishException;
import org.phong.horizon.core.factories.GsonFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
@AllArgsConstructor
public class AblyService {
    private final AblyRealtime ablyRealtime;
    private static final Gson gson = GsonFactory.create();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Publishes a message to a specific Ably channel.
     * The `clientId` parameter can be null.
     * If `clientId` is not null, `message.clientId` will be set, enabling client-side filtering.
     * If `clientId` is null, `message.clientId` will not be set, meaning the message
     * will be delivered to ALL subscribers (useful for system-wide broadcasts).
     *
     * @param channelName The name of the Ably channel.
     * @param eventName   The name of the event.
     * @param data        The actual data payload of the event. This will be converted to JSON.
     * @param clientId    The clientId of the user who initiated this event, or null for system events.
     */
    public void publishMessage(String channelName, String eventName, Map<String, Object> data, String clientId) {
        try {
            Channel channel = ablyRealtime.channels.get(channelName);

            JsonElement messageDataJson = gson.toJsonTree(data);
            Message message = new Message(eventName, messageDataJson);

            if (clientId != null) {
                message.clientId = clientId;
            }
            channel.publish(message, new CompletionListener() {
                @Override
                public void onSuccess() {
                    log.info("Message published successfully to channel: {}", channelName);
                }

                @Override
                public void onError(ErrorInfo reason) {
                    log.error("Failed to publish message to channel: {}. Error: {}", channelName, reason.message);
                }
            });
        } catch (AblyException e) {
            throw new AblyPublishException("Failed to publish to Ably", e);
        } catch (Exception e) {
            throw new AblyPublishException("Failed to serialize message data", e);
        }
    }

    /**
     * Publish multiple messages to a single channel.
     *
     * @param channelName target channel
     * @param messages    list of messages to publish
     */
    public void publishMessages(String channelName, List<Message> messages) {
        try {
            Channel channel = ablyRealtime.channels.get(channelName);
            Message[] messageArray = messages.toArray(new Message[0]);
            channel.publish(messageArray, new CompletionListener() {
                @Override
                public void onSuccess() {
                    log.info("Messages published successfully to channel: {}", channelName);
                }

                @Override
                public void onError(ErrorInfo reason) {
                    log.error("Failed to publish messages to channel: {}. Error: {}", channelName, reason.message);
                }
            });
        } catch (AblyException e) {
            throw new AblyPublishException("Failed to publish to Ably", e);
        }
    }

    /**
     * Publish messages to multiple channels concurrently.
     *
     * @param channelMessages map of channel name to messages
     */
    public void publishToMultipleChannels(Map<String, List<Message>> channelMessages) {
        CompletableFuture.allOf(
                channelMessages.entrySet().stream()
                        .map(entry -> CompletableFuture.runAsync(
                                () -> publishMessages(entry.getKey(), entry.getValue()),
                                executorService
                        ))
                        .toArray(CompletableFuture[]::new)
        ).join();
    }
}
