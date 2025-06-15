package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.phong.horizon.core.enums.InteractionType;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class CommentInteractionCreated extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID commentId;
    private final UUID postId;
    private final UUID userId;
    private final InteractionType interactionType;

    public CommentInteractionCreated(Object source, UUID commentId, UUID postId, UUID userId, InteractionType interactionType) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.interactionType = interactionType;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.commentsUnderPost(postId);
    }

    @Override
    public String getEventName() {
        return "comment.interaction.created";
    }
}
