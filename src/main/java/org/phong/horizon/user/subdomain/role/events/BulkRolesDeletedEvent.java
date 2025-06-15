package org.phong.horizon.user.subdomain.role.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

/**
 * Event fired when multiple roles are deleted in bulk
 */
@Getter
public class BulkRolesDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> roleIds;

    public BulkRolesDeletedEvent(Object source, List<UUID> roleIds) {
        super(source);
        this.roleIds = roleIds;
    }

    @Override
    public String getChannelName() {
        return "roles";
    }

    @Override
    public String getEventName() {
        return "role.bulk.deleted";
    }
}
