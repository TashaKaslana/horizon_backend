package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.enums.PostStatus;
import org.phong.horizon.post.utils.PostChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkPostsUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> postIds;
    private final PostStatus status;
    private final Visibility visibility;
    private final UUID categoryId;

    public BulkPostsUpdatedEvent(Object source, List<UUID> postIds, PostStatus status, Visibility visibility, UUID categoryId) {
        super(source);
        this.postIds = postIds;
        this.status = status;
        this.visibility = visibility;
        this.categoryId = categoryId;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.posts();
    }

    @Override
    public String getEventName() {
        return "posts.bulk-updated";
    }
}
