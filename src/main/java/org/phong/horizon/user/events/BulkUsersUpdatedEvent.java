package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.enums.UserStatus;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkUsersUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> userIds;
    private final UserStatus status;
    private final UUID roleId;

    public BulkUsersUpdatedEvent(Object source, List<UUID> userIds, UserStatus status, UUID roleId) {
        super(source);
        this.userIds = userIds;
        this.status = status;
        this.roleId = roleId;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.users();
    }

    @Override
    public String getEventName() {
        return "users.bulk-updated";
    }
}
