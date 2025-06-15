package org.phong.horizon.user.subdomain.role.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event fired when a role is deleted
 */
@Getter
public class RoleDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID roleId;

    public RoleDeletedEvent(Object source, UUID roleId) {
        super(source);
        this.roleId = roleId;
    }

    @Override
    public String getChannelName() {
        return "roles";
    }

    @Override
    public String getEventName() {
        return "role.deleted";
    }
}
