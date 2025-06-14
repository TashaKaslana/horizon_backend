package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.utils.PostChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkPostsDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> postIds;

    public BulkPostsDeletedEvent(Object source, List<UUID> postIds) {
        super(source);
        this.postIds = postIds;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.posts();
    }

    @Override
    public String getEventName() {
        return "posts.bulk-deleted";
    }
}
