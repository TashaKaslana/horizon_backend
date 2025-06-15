package org.phong.horizon.user.subdomain.role.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.subdomain.role.dtos.RoleDto;
import org.springframework.context.ApplicationEvent;

/**
 * Event fired when a new role is created
 */
@Getter
public class RoleCreatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final RoleDto role;

    public RoleCreatedEvent(Object source, RoleDto role) {
        super(source);
        this.role = role;
    }

    @Override
    public String getChannelName() {
        return "roles";
    }

    @Override
    public String getEventName() {
        return "role.created";
    }
}
