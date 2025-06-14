package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class CommentMentionDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID commentId;
    private final List<UUID> mentionUserIds;

    public CommentMentionDeletedEvent(Object source, UUID commentId, List<UUID> mentionUserIds) {
        super(source);
        this.commentId = commentId;
        this.mentionUserIds = mentionUserIds;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.comment(commentId);
    }

    @Override
    public String getEventName() {
        return "comment.mention.deleted";
    }
}
