package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Getter
public final class CommentDeleted extends ApplicationEvent implements Serializable, AblyPublishableEvent {
    private final UUID commentId;
    private final UUID postId;
    private final UUID userId;

    public CommentDeleted(Object source, UUID commentId, UUID postId, UUID userId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.comment(commentId);
    }

    @Override
    public String getEventName() {
        return "comment.deleted";
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of(
            "commentId", commentId,
            "postId", postId,
            "userId", userId
        );
    }
}
