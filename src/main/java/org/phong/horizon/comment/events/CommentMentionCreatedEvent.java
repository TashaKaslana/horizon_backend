package org.phong.horizon.comment.events;

import lombok.Getter;
import org.phong.horizon.ably.event.AblyPublishableEvent;
import org.phong.horizon.comment.utils.CommentChannelNames;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.UUID;

@Getter
public class CommentMentionCreatedEvent extends ApplicationEvent implements AblyPublishableEvent {
    private final UUID commentId;
    private final UUID postId;
    private final String authorUsername;
    private final UUID authorId;
    private final String content;
    private final Map<String, UUID> mapUsernameToUserId;

    public CommentMentionCreatedEvent(Object source,
                                      UUID commentId,
                                      UUID postId,
                                      String authorUsername,
                                      UUID authorId,
                                      String content,
                                      Map<String, UUID> mapUsernameToUserId) {
        super(source);
        this.commentId = commentId;
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.authorId = authorId;
        this.content = content;
        this.mapUsernameToUserId = mapUsernameToUserId;
    }

    @Override
    public String getChannelName() {
        return CommentChannelNames.comment(commentId);
    }

    @Override
    public String getEventName() {
        return "comment.mention.created";
    }
}
