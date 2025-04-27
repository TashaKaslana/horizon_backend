package org.phong.horizon.comment.listeners;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.phong.horizon.comment.events.ReportCommentCreated;
import org.phong.horizon.notification.dtos.CreateNotificationRequest;
import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.notification.events.CreateNotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentReportListener {
    ApplicationEventPublisher publisher;

    @EventListener
    @Async
    @TransactionalEventListener
    public void onReportCommentCreated(ReportCommentCreated event) {
        publisher.publishEvent(new CreateNotificationEvent(
                this,
                event.getReporterId(),
                new CreateNotificationRequest(
                        event.getReportedUserId(),
                        null,
                        event.getCommentId(),
                        String.format("User %s reported your comment for reason: %s", event.getReporterId(), event.getReason()),
                        NotificationType.REPORT_COMMENT,
                        null
                )
        ));
    }
}
