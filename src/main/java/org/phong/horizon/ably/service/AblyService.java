package org.phong.horizon.ably.service;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.types.AblyException;
import lombok.AllArgsConstructor;
import org.phong.horizon.ably.exception.AblyPublishException; // Import custom exception
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AblyService {
    private final AblyRealtime ablyRealtime;

    public void publishMessage(String channelName, String eventName, Object data) {
        try {
            Channel channel = ablyRealtime.channels.get(channelName);
            channel.publish(eventName, data);
        } catch (AblyException e) {
            throw new AblyPublishException("Error publishing message to Ably channel: " + channelName, e);
        }
    }
}
