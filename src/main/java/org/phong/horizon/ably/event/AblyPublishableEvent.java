package org.phong.horizon.ably.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.phong.horizon.core.utils.ObjectConversion;

import java.util.Map;

public interface AblyPublishableEvent {

    String getChannelName();

    String getEventName();

    /**
     * Default implementation using centralized converter.
     * Override only if you need to customize the payload.
     * <p>IMPORTANT: If you override this, you must annotate it with @JsonIgnore.
     */
    @JsonIgnore
    default Map<String, Object> getPayload() {
        return ObjectConversion.convertObjectToMap(this);
    }
}
