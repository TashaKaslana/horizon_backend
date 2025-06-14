package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.utils.PostChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PostDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID postId;
    private final UUID userId;

    public PostDeletedEvent(Object source, UUID postId, UUID userId) {
        super(source);
        this.postId = postId;
        this.userId = userId;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.post(postId);
    }

    @Override
    public String getEventName() {
        return "post.deleted";
    }
}
