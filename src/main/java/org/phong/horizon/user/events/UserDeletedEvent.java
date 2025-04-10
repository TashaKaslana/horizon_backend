package org.phong.horizon.user.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserDeletedEvent extends ApplicationEvent {
    private final UUID userId;
    private final String username;
    private final String email;

    public UserDeletedEvent(Object source, UUID userId, String username, String email) {
        super(source);
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
