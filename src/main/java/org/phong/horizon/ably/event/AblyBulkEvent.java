package org.phong.horizon.ably.event;

import java.util.List;
import java.util.Map;

/**
 * Marker interface describing a collection of publishable events grouped by channel.
 */
public interface AblyBulkEvent {
    Map<String, List<AblyPublishableEvent>> getChannelEvents();
}

