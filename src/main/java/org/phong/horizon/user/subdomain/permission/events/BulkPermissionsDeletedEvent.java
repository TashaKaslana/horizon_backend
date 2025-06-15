package org.phong.horizon.user.subdomain.permission.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

/**
 * Event fired when multiple permissions are deleted in bulk
 */
@Getter
public class BulkPermissionsDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> permissionIds;

    public BulkPermissionsDeletedEvent(Object source, List<UUID> permissionIds) {
        super(source);
        this.permissionIds = permissionIds;
    }

    @Override
    public String getChannelName() {
        return "permissions";
    }

    @Override
    public String getEventName() {
        return "permission.bulk.deleted";
    }
}
