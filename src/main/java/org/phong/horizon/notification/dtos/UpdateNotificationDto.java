package org.phong.horizon.notification.dtos;

import org.phong.horizon.notification.enums.NotificationType;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for {@link org.phong.horizon.notification.infrastructure.persistence.entities.Notification}
 */
public record UpdateNotificationDto(
        String content,
        NotificationType type,
        Map<String, Object> extraData,
        Boolean isRead,
        Boolean isDeleted) implements Serializable {
}