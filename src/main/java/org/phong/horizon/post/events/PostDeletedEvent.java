package org.phong.horizon.post.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PostDeletedEvent extends ApplicationEvent {
    private final UUID postId;
    private final UUID userId;

    public PostDeletedEvent(Object source, UUID postId, UUID userId) {
        super(source);
        this.postId = postId;
        this.userId = userId;
    }
}
