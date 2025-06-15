package org.phong.horizon.post.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.post.utils.PostChannelNames;
import org.phong.horizon.core.enums.InteractionType;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PostInteractionDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID postId;
    private final UUID userId;
    private final InteractionType interactionType;

    public PostInteractionDeletedEvent(Object source, UUID postId, UUID userId, InteractionType interactionType) {
        super(source);
        this.postId = postId;
        this.userId = userId;
        this.interactionType = interactionType;
    }

    @Override
    public String getChannelName() {
        return PostChannelNames.post(postId);
    }

    @Override
    public String getEventName() {
        return "post.interaction.deleted";
    }
}
