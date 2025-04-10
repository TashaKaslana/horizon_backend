package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link Comment}
 */
@Getter
public final class CommentCreated extends ApplicationEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final UUID id;
    private final UUID postId;
    private final UUID userId;
    private final String content;

    public CommentCreated(Object source, UUID id, UUID postId, UUID userId, String content) {
        super(source);
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }
}