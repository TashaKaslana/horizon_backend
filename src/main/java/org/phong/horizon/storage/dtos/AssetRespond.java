package org.phong.horizon.storage.dtos;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.storage.infrastructure.persistence.entities.Asset}
 */
public record AssetRespond(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id, String publicId, String secureUrl, String resourceType, String format, Long bytes, Integer width, Integer height, String originalFilename) implements Serializable {
  }