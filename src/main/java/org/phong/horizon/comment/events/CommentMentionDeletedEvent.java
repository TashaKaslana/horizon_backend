package org.phong.horizon.comment.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class CommentMentionDeletedEvent extends ApplicationEvent {
    private final UUID commentId;
    private final List<UUID> mentionUserIds;

    public CommentMentionDeletedEvent(Object source, UUID commentId, List<UUID> mentionUserIds) {
        super(source);
        this.commentId = commentId;
        this.mentionUserIds = mentionUserIds;
    }
}
