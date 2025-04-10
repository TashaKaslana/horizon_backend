package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.core.dtos.FieldValueChange;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Getter
public final class CommentUpdated extends ApplicationEvent implements Serializable {
    private final UUID commentId;
    private final UUID postId;
    private final UUID userId;
    private final String content;
    private final Map<String, FieldValueChange> additionalInfo;

    public CommentUpdated(Object source,
                          UUID commentId,
                          UUID postId,
                          UUID userId,
                          String content,
                          Map<String, FieldValueChange> additionalInfo) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.additionalInfo = additionalInfo;
    }
}
