package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link Comment}
 */
@Getter
public final class CommentCreated extends ApplicationEvent implements Serializable, AblyPublishableEvent {
    private final UUID id;
    private final UUID postId;
    private final UUID userId;
    private final String content;
    private final UUID currentUserId;

    public CommentCreated(Object source, UUID id, UUID postId, UUID userId, String content, UUID currentUserId) {
        super(source);
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.currentUserId = currentUserId;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.commentsUnderPost(postId);
    }

    @Override
    public String getEventName() {
        return "comment.created";
    }

    @Override
    public Map<String, Object> getPayload() {
        return Map.of(
                "id", id,
                "postId", postId,
                "userId", userId,
                "content", content,
                "currentUserId", currentUserId
        );
    }
}