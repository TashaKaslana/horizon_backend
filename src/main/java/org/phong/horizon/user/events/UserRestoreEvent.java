package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserRestoreEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID userId;

    public UserRestoreEvent(Object source, UUID id) {
        super(source);
        this.userId = id;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.user(userId);
    }

    @Override
    public String getEventName() {
        return "user.restored";
    }
}
