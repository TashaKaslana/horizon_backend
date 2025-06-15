package org.phong.horizon.user.subdomain.permission.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event fired when a permission is deleted
 */
@Getter
public class PermissionDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID permissionId;

    public PermissionDeletedEvent(Object source, UUID permissionId) {
        super(source);
        this.permissionId = permissionId;
    }

    @Override
    public String getChannelName() {
        return "permissions";
    }

    @Override
    public String getEventName() {
        return "permission.deleted";
    }
}
