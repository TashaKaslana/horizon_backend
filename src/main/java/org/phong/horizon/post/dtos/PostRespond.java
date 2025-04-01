package org.phong.horizon.post.dtos;

import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infraustructure.persistence.entities.Post}
 */
public record PostRespond(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id, UserSummaryRespond user,
                          String caption, String description, String videoUrl, String thumbnailUrl, Double duration, String visibility,
                          List<String> tags) implements Serializable {
}