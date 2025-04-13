package org.phong.horizon.storage.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link org.phong.horizon.storage.infrastructure.persistence.entities.Asset}
 */
public record UploadCompleteRequest(
        @NotBlank String publicId,
        @URL String secureUrl,
        @NotBlank String resourceType,
        @NotBlank String format,
        @NotNull Long bytes,
        @NotNull Integer width,
        @NotNull Integer height,
        @NotBlank String originalFilename) implements Serializable {
}