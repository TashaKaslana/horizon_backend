package org.phong.horizon.ably.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

/**
 * Basic implementation of {@link AblyBulkEvent} to easily publish a map of channel events.
 */
@Getter
public class DefaultAblyBulkEvent extends ApplicationEvent implements AblyBulkEvent {
    private final Map<String, List<AblyPublishableEvent>> channelEvents;

    public DefaultAblyBulkEvent(Object source, Map<String, List<AblyPublishableEvent>> channelEvents) {
        super(source);
        this.channelEvents = channelEvents;
    }
}
