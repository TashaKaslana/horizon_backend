package org.phong.horizon.user.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.user.utils.UserChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkUsersDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> userIds;

    public BulkUsersDeletedEvent(Object source, List<UUID> userIds) {
        super(source);
        this.userIds = userIds;
    }

    @Override
    public String getChannelName() {
        return UserChannelNames.users();
    }

    @Override
    public String getEventName() {
        return "users.bulk-deleted";
    }
}
