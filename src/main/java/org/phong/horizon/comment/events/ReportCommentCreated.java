package org.phong.horizon.comment.events;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ReportCommentCreated extends ApplicationEvent {
    UUID id;
    UUID commentId;
    UUID reporterId;
    UUID reportedUserId;
    String reason;

    public ReportCommentCreated(Object source, UUID id, UUID commentId, UUID reporterId, UUID reportedUserId, String reason) {
        super(source);
        this.id = id;
        this.commentId = commentId;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.reason = reason;
    }
}
