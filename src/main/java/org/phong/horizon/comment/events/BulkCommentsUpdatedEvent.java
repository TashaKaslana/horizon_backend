package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.enums.CommentStatus;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;

@Getter
public class BulkCommentsUpdatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final List<UUID> commentIds;
    private final CommentStatus status;

    public BulkCommentsUpdatedEvent(Object source, List<UUID> commentIds, CommentStatus status) {
        super(source);
        this.commentIds = commentIds;
        this.status = status;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.comments();
    }

    @Override
    public String getEventName() {
        return "comments.bulk-updated";
    }
}
