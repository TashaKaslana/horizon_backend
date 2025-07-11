package org.phong.horizon.report.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.phong.horizon.admin.notification.infrastructure.dtos.CreateAdminNotification;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationRelatedType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.phong.horizon.admin.notification.events.CreateAdminNotificationEvent;
import org.phong.horizon.report.events.ReportCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
@Slf4j
public class ReportListener {
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Async
    @TransactionalEventListener
    public void onReportCreated(ReportCreatedEvent event) {
        String title;
        String message;
        NotificationRelatedType relatedType;
        NotificationSeverity severity = NotificationSeverity.WARNING;

        switch (event.getItemType()) {
            case POST:
                title = "New Post Report";
                message = String.format("User %s reported post %s for reason: %s. Reported user: %s",
                        event.getReporterId(), event.getReportedItemId(), event.getReason(), event.getActualReportedUserId());
                relatedType = NotificationRelatedType.POST;
                break;
            case COMMENT:
                title = "New Comment Report";
                message = String.format("User %s reported comment %s for reason: %s. Reported user: %s",
                        event.getReporterId(), event.getReportedItemId(), event.getReason(), event.getActualReportedUserId());
                relatedType = NotificationRelatedType.COMMENT;
                break;
            case USER:
                title = "New User Report";
                message = String.format("User %s reported user %s for reason: %s.",
                        event.getReporterId(), event.getReportedItemId(), event.getReason());
                relatedType = NotificationRelatedType.USER;
                break;
            default:
                log.warn("Unhandled item type in ReportListener for admin notification: {}", event.getItemType());
                return;
        }

        CreateAdminNotification adminNotificationDto = new CreateAdminNotification(
                title,
                message,
                AdminNotificationType.REPORT,
                severity,
                "ReportService",
                relatedType,
                event.getReportedItemId()
        );

        try {
            eventPublisher.publishEvent(new CreateAdminNotificationEvent(this, adminNotificationDto));
            log.debug("CreateAdminNotificationEvent published for reportId: {}", event.getReportId());
        } catch (Exception e) {
            log.error("Failed to publish CreateAdminNotificationEvent for reportId: {}. Error: {}", event.getReportId(), e.getMessage(), e);
        }
    }
}
