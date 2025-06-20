package org.phong.horizon.post.dtos;

import jakarta.validation.constraints.NotNull;
import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;
import org.phong.horizon.post.enums.PostStatus;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record CreatePostRequest(@NotNull String caption, String description, UploadCompleteRequest videoAsset,
                                Double duration,
                                @NotNull Visibility visibility, List<String> tags,
                                @NotNull String categoryName, PostStatus status) implements Serializable {
}

