package org.phong.horizon.comment.events;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentPinned extends ApplicationEvent implements AblyPublishableEvent {
    UUID commentId;
    UUID postId;
    UUID pinnerId;
    UUID pinnedUserId;

    public CommentPinned(Object source, UUID commentId, UUID postId, UUID pinnerId, UUID pinnedUserId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.pinnerId = pinnerId;
        this.pinnedUserId = pinnedUserId;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.commentsUnderPost(postId);
    }

    @Override
    public String getEventName() {
        return "comment.pinned";
    }
}
