package org.phong.horizon.post.dtos;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.post.infrastructure.persistence.entities.Post;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Post}
 */
public record PostCloneDto(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id, UUID userId, String caption, String description, UUID videoAssetId, Double duration, Visibility visibility, List<String> tags) implements Serializable {
  }