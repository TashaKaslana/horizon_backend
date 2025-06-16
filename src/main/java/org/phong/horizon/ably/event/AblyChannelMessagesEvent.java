package org.phong.horizon.ably.event;

import java.util.List;

/**
 * Represents multiple Ably publishable events targeting a single channel.
 */
public interface AblyChannelMessagesEvent {
    String getChannelName();

    List<AblyPublishableEvent> getEvents();
}
