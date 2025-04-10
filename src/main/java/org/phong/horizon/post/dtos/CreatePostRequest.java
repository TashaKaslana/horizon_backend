package org.phong.horizon.post.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record CreatePostRequest(String caption, String description, Double duration, String visibility,
                                List<String> tags, UUID videoAssetId) implements Serializable {
}