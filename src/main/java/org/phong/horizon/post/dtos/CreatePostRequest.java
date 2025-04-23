package org.phong.horizon.post.dtos;

import org.phong.horizon.core.enums.Visibility;
import org.phong.horizon.storage.dtos.UploadCompleteRequest;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record CreatePostRequest(UUID userId,
                                String caption, String description, UploadCompleteRequest videoAsset, Double duration,
                                Visibility visibility, List<String> tags) implements Serializable {
}