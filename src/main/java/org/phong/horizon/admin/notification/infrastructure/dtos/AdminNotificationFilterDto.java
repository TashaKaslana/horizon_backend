package org.phong.horizon.admin.notification.infrastructure.dtos;

import lombok.Data;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Data
public class AdminNotificationFilterDto {
    private AdminNotificationType type;
    private NotificationSeverity severity;
    private Boolean isRead;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdAfter;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime createdBefore;
    private String title;
    private String message;
}

