package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkCommentsDeletedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> commentIds;

    public BulkCommentsDeletedEvent(Object source, List<UUID> commentIds) {
        super(source);
        this.commentIds = commentIds;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.comments();
    }

    @Override
    public String getEventName() {
        return "comments.bulk-deleted";
    }
}
