package org.phong.horizon.report.dto;

import lombok.Data;
import org.phong.horizon.notification.dtos.NotificationResponse;
import org.phong.horizon.post.dtos.PostSummaryResponse;
import org.phong.horizon.report.enums.ModerationItemType;
import org.phong.horizon.report.enums.ModerationStatus;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.time.OffsetDateTime;
import java.util.UUID;
/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.Comment}
 */
@Data
public class ReportDto {
    private UUID id;
    private String reason;
    private String moderatorNotes;
    private ModerationStatus status;
    private ModerationItemType itemType;
    private PostSummaryResponse post;
    private NotificationResponse.CommentDto comment;
    private UserSummaryRespond reportedUser;
    private UserSummaryRespond reporter;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

