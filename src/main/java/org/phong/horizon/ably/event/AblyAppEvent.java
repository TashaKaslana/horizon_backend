package org.phong.horizon.ably.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AblyAppEvent extends ApplicationEvent {
    private final String channelName;
    private final String eventName;
    private final Object data;

    public AblyAppEvent(Object source, String channelName, String eventName, Object data) {
        super(source);
        this.channelName = channelName;
        this.eventName = eventName;
        this.data = data;
    }
}

