package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.phong.horizon.user.dtos.UserOverview;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link Comment}
 */
@Getter
public final class CommentCreated extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID id;
    private final UUID postId;
    private final UserOverview user;
    private final String content;
    private final UUID parentCommentId;
    private final Instant createdAt;
    private final UUID currentUserId;

    public CommentCreated(Object source, UUID id, UUID postId,
                          UserOverview user, String content, UUID parentCommentId,
                          UUID currentUserId, Instant createdAt) {
        super(source);
        this.id = id;
        this.postId = postId;
        this.user = user;
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.currentUserId = currentUserId;
        this.createdAt = createdAt;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.commentsUnderPost(postId);
    }

    @Override
    public String getEventName() {
        return "comment.created";
    }

//    @Override
//    public Map<String, Object> getPayload() {
//        return Map.of(
//                "id", id,
//                "postId", postId,
//                "user", Map.of(
//                        "id", user.id(),
//                        "displayName", user.displayName(),
//                        "username", user.username(),
//                        "profileImage", user.profileImage()
//                ),
//                "content", content,
//                "parentCommentId", parentCommentId,
//                "createdAt", createdAt.toString()
//        );
//    }
}