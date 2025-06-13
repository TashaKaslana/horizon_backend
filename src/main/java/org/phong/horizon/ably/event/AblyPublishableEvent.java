package org.phong.horizon.ably.event;

import java.util.Map;

public interface AblyPublishableEvent {
    String getChannelName();
    String getEventName();
    Map<String, Object> getPayload();
}
