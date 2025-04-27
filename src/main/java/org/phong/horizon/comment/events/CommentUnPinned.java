package org.phong.horizon.comment.events;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentUnPinned extends ApplicationEvent {
    UUID commentId;
    UUID postId;
    UUID unpinnerId;
    UUID unpinnedUserId;

    public CommentUnPinned(Object source, UUID commentId, UUID postId, UUID unpinnerId, UUID unpinnedUserId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.unpinnerId = unpinnerId;
        this.unpinnedUserId = unpinnedUserId;
    }
}
