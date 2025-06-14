package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID userId;
    private final String username;
    private final String email;

    public UserDeletedEvent(Object source, UUID userId, String username, String email) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.user(userId);
    }

    @Override
    public String getEventName() {
        return "user.deleted";
    }
}
