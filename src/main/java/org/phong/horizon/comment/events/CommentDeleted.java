package org.phong.horizon.comment.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.UUID;

@Getter
public final class CommentDeleted extends ApplicationEvent implements Serializable {
    private final UUID commentId;
    private final UUID postId;
    private final UUID userId;

    public CommentDeleted(Object source, UUID commentId, UUID postId, UUID userId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }
}
