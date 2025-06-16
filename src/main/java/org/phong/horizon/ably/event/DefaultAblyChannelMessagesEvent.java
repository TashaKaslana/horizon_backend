package org.phong.horizon.ably.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Simple implementation of {@link AblyChannelMessagesEvent}.
 */
@Getter
public class DefaultAblyChannelMessagesEvent extends ApplicationEvent implements AblyChannelMessagesEvent {
    private final String channelName;
    private final List<AblyPublishableEvent> events;

    public DefaultAblyChannelMessagesEvent(Object source, String channelName, List<AblyPublishableEvent> events) {
        super(source);
        this.channelName = channelName;
        this.events = events;
    }
}
