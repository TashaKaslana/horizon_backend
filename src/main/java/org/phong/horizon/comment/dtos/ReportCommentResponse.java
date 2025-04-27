package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotNull;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.ReportComment}
 */
public record ReportCommentResponse(UUID id, @NotNull CommentRespond comment, @NotNull UserSummaryRespond reporter,
                                    @NotNull String reason, Instant createdAt,
                                    Instant updatedAt) implements Serializable {
}