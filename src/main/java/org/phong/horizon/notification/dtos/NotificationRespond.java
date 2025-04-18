package org.phong.horizon.notification.dtos;

import org.phong.horizon.notification.enums.NotificationType;
import org.phong.horizon.post.dtos.PostRespond;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.notification.infrastructure.persistence.entities.Notification}
 */
public record NotificationRespond(UUID id, UserSummaryRespond recipientUser, UserSummaryRespond senderUser,
                                  PostRespond post, CommentDto comment, String content, NotificationType type,
                                  Map<String, Object> extraData, Boolean isRead, Boolean isDeleted,
                                  OffsetDateTime createdAt, OffsetDateTime deletedAt) implements Serializable {
    /**
     * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
     */
    public record CommentDto(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id,
                             String content) implements Serializable {
    }
}