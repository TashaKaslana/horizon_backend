package org.phong.horizon.post.dtos;

import org.phong.horizon.storage.dtos.AssetRespond;
import org.phong.horizon.user.dtos.UserSummaryRespond;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link org.phong.horizon.post.infrastructure.persistence.entities.Post}
 */
public record PostRespond(Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy, UUID id, UserSummaryRespond user,
                          String caption, String description, Double duration, String visibility,
                          List<String> tags, String videoPlaybackUrl, String videoThumbnailUrl, AssetRespond videoAsset) implements Serializable {
}