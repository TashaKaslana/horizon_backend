package org.phong.horizon.notification.dtos;

import org.phong.horizon.notification.enums.NotificationType;

import java.time.OffsetDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFilterCriteria {
    private NotificationType type;
    private Boolean isRead;
    private Boolean isDeleted;
    private Boolean includeRecipientUser;
    private OffsetDateTime createdAt;
    private OffsetDateTime deletedAt;
}
