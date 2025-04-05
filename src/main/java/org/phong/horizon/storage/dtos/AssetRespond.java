package org.phong.horizon.storage.dtos;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.storage.infrastructure.persistence.entities.Asset}
 */
public record AssetRespond(Instant createdAt, UUID createdBy, UUID id, String publicId, String resourceType,
                           String format, Long bytes, Integer width, Integer height,
                           String originalFilename) implements Serializable {
}