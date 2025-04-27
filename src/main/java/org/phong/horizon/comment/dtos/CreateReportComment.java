package org.phong.horizon.comment.dtos;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.comment.infrastructure.persistence.entities.ReportComment}
 */
public record CreateReportComment(UUID commentId, @NotNull String reason) implements Serializable {
}