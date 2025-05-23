package org.phong.horizon.admin.notification.infrastructure.dtos;

import org.phong.horizon.admin.notification.enums.AdminNotificationType;
import org.phong.horizon.admin.notification.enums.NotificationRelatedType;
import org.phong.horizon.admin.notification.enums.NotificationSeverity;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.admin.notification.infrastructure.entities.AdminNotification}
 */
public record CreateAdminNotification(String title, String message, AdminNotificationType type,
                                      NotificationSeverity severity, String source, NotificationRelatedType relatedType,
                                      UUID relatedId) implements Serializable {
}