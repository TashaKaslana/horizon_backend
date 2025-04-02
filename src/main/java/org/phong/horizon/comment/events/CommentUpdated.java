package org.phong.horizon.comment.events;

import lombok.Builder;
import org.phong.horizon.comment.infrastructure.persistence.entities.Comment;
import org.phong.horizon.infrastructure.events.Event;

@Builder
public record CommentUpdated(Comment comment) implements Event {
}
