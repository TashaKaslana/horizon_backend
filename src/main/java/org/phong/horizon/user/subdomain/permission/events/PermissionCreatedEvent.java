package org.phong.horizon.user.subdomain.permission.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.subdomain.permission.dtos.PermissionDto;
import org.springframework.context.ApplicationEvent;


/**
 * Event fired when a new permission is created
 */
@Getter
public class PermissionCreatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final PermissionDto permission;

    public PermissionCreatedEvent(Object source, PermissionDto permission) {
        super(source);
        this.permission = permission;
    }

    @Override
    public String getChannelName() {
        return "permissions";
    }

    @Override
    public String getEventName() {
        return "permission.created";
    }
}
