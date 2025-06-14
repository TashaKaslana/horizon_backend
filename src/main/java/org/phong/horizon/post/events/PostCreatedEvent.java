package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.utils.PostChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PostCreatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID postId;
    private final UUID userId;
    private final String title;
    private final String content;

    public PostCreatedEvent(Object source, UUID postId, UUID userId, String title, String content) {
        super(source);
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.posts();
    }

    @Override
    public String getEventName() {
        return "post.created";
    }
}
