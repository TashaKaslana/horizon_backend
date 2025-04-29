package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.PostReport}
 */
public record PostReportResponse(UUID id, @NotNull String reason, Instant createdAt, @NotNull UserSummaryRespond user,
                                 @NotNull PostSummaryResponse post) implements Serializable {
}