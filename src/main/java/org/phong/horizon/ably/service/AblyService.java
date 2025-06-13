package org.phong.horizon.ably.service;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.ably.exception.AblyPublishException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AblyService {
    private final AblyRealtime ablyRealtime;
    private final ObjectMapper objectMapper;

    public AblyService(AblyRealtime ablyRealtime, ObjectMapper objectMapper) {
        this.ablyRealtime = ablyRealtime;
        this.objectMapper = objectMapper;
    }

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
    public void publishMessage(String channelName, String eventName, Object data, String clientId) {
        log.debug("Publishing to '{}' with event '{}' for client '{}'", channelName, eventName, clientId);

        try {
            Channel channel = ablyRealtime.channels.get(channelName);

            ObjectNode messageDataNode = objectMapper.valueToTree(data);

            Message message = new Message(eventName, messageDataNode);
            if (clientId != null) {
                message.clientId = clientId;
            }

            channel.publish(message);
            log.debug("Successfully published '{}'", eventName);

        } catch (AblyException e) {
            throw new AblyPublishException("Failed to publish to Ably", e);
        }
    }
}
