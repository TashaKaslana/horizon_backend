package org.phong.horizon.storage.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.storage.infrastructure.persistence.entities.Asset}
 */
public record UploadCompleteRequest(String publicId, String secureUrl, String resourceType, String format, Long bytes, Integer width,
                       Integer height, String originalFilename) implements Serializable {
}