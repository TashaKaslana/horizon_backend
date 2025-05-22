package org.phong.horizon.admin.notification.infrastructure.dtos;

import lombok.Data;
import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationRelatedType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class AdminNotificationDto {
    private UUID id;
    private String title;
    private String message;
    private AdminNotificationType type;
    private NotificationSeverity severity;
    private String source;
    private NotificationRelatedType relatedType;
    private UUID relatedId;
    private Boolean isRead;
    private OffsetDateTime createdAt;
}

