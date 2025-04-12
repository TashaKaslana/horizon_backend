package org.phong.horizon.user.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserRestoreEvent extends ApplicationEvent {
    private final UUID userId;

    public UserRestoreEvent(Object source, UUID id) {
        super(source);
        this.userId = id;
    }
}
