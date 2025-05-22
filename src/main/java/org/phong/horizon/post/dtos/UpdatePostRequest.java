package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.phong.horizon.post.enums.PostStatus;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record UpdatePostRequest(
        @NotBlank @Size(max = 255) String caption,
        @NotBlank String description,
        @NotBlank @Size(max = 10) String visibility,
        List<String> tags,
        @NotNull String categoryName,
        PostStatus status) implements Serializable {
}

